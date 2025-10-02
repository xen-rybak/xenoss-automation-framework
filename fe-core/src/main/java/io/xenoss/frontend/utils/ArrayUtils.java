package io.xenoss.frontend.utils;

import com.microsoft.playwright.Locator;
import io.xenoss.frontend.components.ElementsContainer;
import io.xenoss.frontend.elements.ElementsList;
import io.xenoss.frontend.elements.LocatorsList;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ArrayUtils {
    public static <T extends ElementsContainer> Collector<T, ?, ElementsList<T>> toElementsList() {
        return new ElementsListCollector<>(ElementsList<T>::new, ElementsList::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH)));
    }

    public static Collector<Locator, ?, LocatorsList> toLocatorsList() {
        return new LocatorsListCollector(LocatorsList::new, LocatorsList::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH)));
    }

    record ElementsListCollector<T extends ElementsContainer>(Supplier<ElementsList<T>> supplier,
                                                              BiConsumer<ElementsList<T>, T> accumulator,
                                                              BinaryOperator<ElementsList<T>> combiner,
                                                              Function<ElementsList<T>, ElementsList<T>> finisher,
                                                              Set<Characteristics> characteristics
    ) implements Collector<T, ElementsList<T>, ElementsList<T>> {

        ElementsListCollector(Supplier<ElementsList<T>> supplier,
                              BiConsumer<ElementsList<T>, T> accumulator,
                              BinaryOperator<ElementsList<T>> combiner,
                              Set<Characteristics> characteristics) {
            this(supplier, accumulator, combiner, i -> i, characteristics);
        }
    }

    record LocatorsListCollector(Supplier<LocatorsList> supplier,
                                 BiConsumer<LocatorsList, Locator> accumulator,
                                 BinaryOperator<LocatorsList> combiner,
                                 Function<LocatorsList, LocatorsList> finisher,
                                 Set<Characteristics> characteristics
    ) implements Collector<Locator, LocatorsList, LocatorsList> {

        LocatorsListCollector(Supplier<LocatorsList> supplier,
                              BiConsumer<LocatorsList, Locator> accumulator,
                              BinaryOperator<LocatorsList> combiner,
                              Set<Characteristics> characteristics) {
            this(supplier, accumulator, combiner, i -> i, characteristics);
        }
    }
}
