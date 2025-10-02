package io.xenoss.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for managing asynchronous tasks with thread pools
 */
public final class ThreadingUtils {

    /**
     * Get a new task executor with specified pool size
     *
     * @param poolSize number of threads in the pool (if 0 or negative, uses available processors)
     * @return TaskExecutor instance
     */
    public static <T> TaskExecutor<T> getExecutor(int poolSize) {
        return new TaskExecutor<>(poolSize);
    }

    /**
     * Start an asynchronous task with a new executor
     *
     * @param code the callable to execute
     * @return TaskInfo representing the running task
     */
    public static <T> TaskInfo<T> startAsync(Callable<T> code) {
        return ThreadingUtils.<T>getExecutor(1)
                             .startAsync(code);
    }

    public static <T> TaskInfo<T> startAsync(Callable<T> code, String threadName, Boolean isDaemon) {
        TaskInfo<T> taskInfo = new TaskInfo<>(RandomUtils.randomNumber(1, Integer.MAX_VALUE), code);
        taskInfo.setStatus(TaskStatus.PENDING);

        Thread thread = new Thread(() -> {
            taskInfo.setStatus(TaskStatus.RUNNING);
            while (!Thread.currentThread()
                          .isInterrupted()) {
                try {
                    code.call();
                } catch (InterruptedException e) {
                    taskInfo.setStatus(TaskStatus.FAILED);
                    Thread.currentThread()
                          .interrupt();
                    break;
                } catch (Exception e) {
                    taskInfo.setStatus(TaskStatus.FAILED);
                    throw new RuntimeException(e);
                }
            }
            taskInfo.setStatus(TaskStatus.FINISHED);
        });

        taskInfo.setThread(thread);

        thread.setName(threadName);
        thread.setDaemon(isDaemon);
        thread.start();

        return taskInfo;
    }

    /**
     * TaskExecutor class for managing thread pools and tasks
     */
    public static class TaskExecutor<T> implements Closeable {
        private final ExecutorService executor;
        private final List<TaskInfo<T>> activeTasks = new CopyOnWriteArrayList<>();
        private final AtomicInteger taskCounter = new AtomicInteger(0);

        /**
         * Create a new TaskExecutor with specified pool size
         *
         * @param poolSize number of threads in the pool
         */
        public TaskExecutor(int poolSize) {
            this.executor = Executors.newFixedThreadPool(poolSize,
                    new ThreadFactory() {
                        private final AtomicInteger threadNumber = new AtomicInteger(1);

                        @Override
                        public Thread newThread(@NotNull Runnable r) {
                            Thread t = new Thread(r, "ThreadingUtils-" + threadNumber.getAndIncrement());
                            t.setDaemon(false);
                            return t;
                        }
                    });
        }

        /**
         * Start an asynchronous task
         *
         * @param code the callable to execute
         * @return TaskInfo representing the running task
         */
        public TaskInfo<T> startAsync(Callable<T> code) {
            TaskInfo<T> taskInfo = new TaskInfo<>(taskCounter.incrementAndGet(), code);
            activeTasks.add(taskInfo);

            Future<T> future = executor.submit(() -> {
                T result = null;
                try {
                    taskInfo.setStatus(TaskStatus.RUNNING);
                    result = code.call();
                    taskInfo.setResult(result);
                    taskInfo.setStatus(TaskStatus.FINISHED);
                } catch (Exception e) {
                    taskInfo.setException(e);
                    taskInfo.setStatus(TaskStatus.FAILED);
                }
                return result;
            });
            taskInfo.setFuture(future);
            return taskInfo;
        }

        /**
         * Get all active tasks
         *
         * @return Collection of all active TaskInfo objects
         */
        public Collection<TaskInfo<T>> getAllTasks() {
            return new ArrayList<>(activeTasks);
        }

        /**
         * Wait for all tasks to finish
         */
        public void waitAllTasksFinished() {
            List<Future<?>> futures = new ArrayList<>();
            for (TaskInfo<T> task : activeTasks) {
                if (task.getFuture() != null) {
                    futures.add(task.getFuture());
                }
            }

            // Wait for all futures to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    Thread.currentThread()
                          .interrupt();
                    throw new RuntimeException("Interrupted while waiting for tasks", e);
                } catch (ExecutionException e) {
                    // Exception already handled in task execution
                }
            }
        }

        public boolean isRunning() {
            return activeTasks.stream().anyMatch((task) -> task.getStatus()
                                                               .equals(TaskStatus.RUNNING));
        }

        public long errorsCount() {
            return activeTasks.stream()
                              .filter((task) -> task.getStatus()
                                                    .equals(TaskStatus.FAILED))
                              .count();
        }

        /**
         * Shutdown the executor service
         */
        public void shutdown() {
            executor.shutdown();
        }

        /**
         * Shutdown the executor service immediately
         */
        public void shutdownNow() {
            executor.shutdownNow();
        }

        @Override
        public void close() {
            executor.close();
        }
    }

    /**
     * TaskInfo class to represent the status and result of an asynchronous task
     */
    @Getter
    @Setter
    @Slf4j
    public static class TaskInfo<T> {
        private final int taskId;
        private final Object taskCode;
        private volatile TaskStatus status = TaskStatus.PENDING;
        private volatile T result;
        private volatile Exception exception;
        private volatile Future<T> future;
        private volatile Thread thread;
        private final AtomicBoolean isWaiting = new AtomicBoolean(false);

        /**
         * Create a new TaskInfo
         *
         * @param taskId   unique identifier for the task
         * @param taskCode the code that was executed (callable)
         */
        public TaskInfo(int taskId, Object taskCode) {
            this.taskId = taskId;
            this.taskCode = taskCode;
        }

        /**
         * Wait for the task to finish
         */
        public void waitFinished() {
            if (future != null) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    Thread.currentThread()
                          .interrupt();
                    throw new RuntimeException("Task interrupted", e);
                } catch (ExecutionException e) {
                    // Exception already handled in task execution
                }
            }
        }

        /**
         * Get the result of the task
         *
         * @return result of the task
         * @throws RuntimeException if task failed or no result available
         */
        public T waitForResult() {
            waitFinished();

            if (status == TaskStatus.FAILED) {
                throw new RuntimeException("Task failed", exception);
            }

            return result;
        }

        public void terminate() {
            if (future != null) {
                future.cancel(true);
            }

            if (thread != null && thread.isAlive()) {
                thread.interrupt();
                try {
                    thread.join(2000); // Wait up to 2 seconds for thread to stop
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Interrupted {} thread", thread.getName());
                }
            }
        }

    }

    /**
     * Enum representing the status of a task
     */
    public enum TaskStatus {
        PENDING,
        RUNNING,
        FINISHED,
        FAILED
    }
}
