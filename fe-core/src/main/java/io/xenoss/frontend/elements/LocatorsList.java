package io.xenoss.frontend.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.PlaywrightException;
import io.xenoss.frontend.components.ElementsContainer;
import io.xenoss.frontend.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LocatorsList extends ArrayList<Locator> {
    public List<String> getElementsText() {
        return this.stream()
                   .map(Locator::textContent)
                   .toList();
    }

    public Locator findElementByExactText(String exactText) {
        return findElementBySpecifiedCriteria(Locator::textContent, exactText);
    }

    public Locator findElementBySpecifiedCriteria(Function<Locator, String> criteria, String expectedValue) {
        var filteredElements = this.stream()
                                   .filter(element -> criteria.apply(element)
                                                              .equals(expectedValue))
                                   .toList();
        if (filteredElements.isEmpty()) {
            throw new PlaywrightException(String.format("Unable to find element by '%s' criteria", expectedValue));
        }

        if (filteredElements.size() > 1) {
            throw new PlaywrightException(String.format("%s elements found by '%s' criteria, but expected: 1", filteredElements.size(), expectedValue));
        }

        return filteredElements.getFirst();
    }

    public <R extends ElementsContainer> ElementsList<R> toElementsList(Function<Locator, R> construct) {
        return this.stream()
                   .map(construct)
                   .collect(ArrayUtils.toElementsList());
    }
}
