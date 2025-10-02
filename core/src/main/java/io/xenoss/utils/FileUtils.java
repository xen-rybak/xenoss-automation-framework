package io.xenoss.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Objects;


@Slf4j
public class FileUtils {
    public static File getResourceFile(String filePath) {
        var classLoader = FileUtils.class.getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(filePath),
                                       String.format("'%s' file has not been found in resource folders", filePath))
                               .getFile());
    }

    public static String getResourceFileAsString(String resourceFilePath) {
        return new String(getResourceFileAsBytes(resourceFilePath), StandardCharsets.UTF_8);
    }

    public static <T> T getResourceFileAsObject(String resourceFilePath, Class<T> tClass) {
        var resourceFile = getResourceFileAsString(resourceFilePath);
        return SerializationUtils.fromJson(resourceFile, tClass);
    }

    @SneakyThrows
    public static byte[] getResourceFileAsBytes(String filePath) {
        var file = getResourceFile(filePath);
        byte[] byteArray = new byte[(int) file.length()];
        try (FileInputStream inputStream = new FileInputStream(file)) {
            inputStream.read(byteArray);
            return byteArray;
        }
    }

    public static void makeDir(String path) {
        var isCreated = new File(path).mkdirs();
        log.info(isCreated ? "{} folder is created" : "{} folder already exists", path);
    }

    @SneakyThrows
    public static String printToFile(String path, String content) {
        try (var out = new PrintWriter(path, StandardCharsets.UTF_8)) {
            out.print(content);
        }
        return toAbsolutePart(path);
    }

    public static String toAbsolutePart(String path) {
        return String.format("file://%s", Paths.get(path)
                                               .toAbsolutePath());
    }
}
