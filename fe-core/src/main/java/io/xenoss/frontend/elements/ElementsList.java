package io.xenoss.frontend.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.PlaywrightException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class ElementsList<T extends ContainElements> extends ArrayList<T> {
    public List<String> getElementsText(Function<T, Locator> getChildWithText) {
        return this.stream()
                   .map(element -> getChildWithText.apply(element)
                                                   .textContent())
                   .toList();
    }

    public T findElementByExactChildText(Function<T, Locator> getIdElement, String expectedIdText) {
        log.info("Looking for element with '{}' text...", expectedIdText);
        log.info("{} elements found, checking text content", this.size());

        var filteredElements = this.stream()
                                   .filter(element -> getIdElement.apply(element)
                                                                  .textContent()
                                                                  .equals(expectedIdText))
                                   .toList();
        if (filteredElements.isEmpty()) {
            throw new PlaywrightException(String.format("Unable to find element by '%s' text", expectedIdText));
        }

        if (filteredElements.size() > 1) {
            throw new PlaywrightException(String.format("%s elements found by '%s' text, but expected: 1", filteredElements.size(), expectedIdText));
        }

        return filteredElements.getFirst();
    }

    public T findElementWithChildContainingText(Function<T, Locator> getIdElement, String idContainsText) {
        log.info("Looking for element with child containing '{}' text...", idContainsText);
        log.info("{} elements found, checking text content", this.size());

        var filteredElements = this.stream()
                                   .filter(element -> getIdElement.apply(element)
                                                                  .textContent()
                                                                  .contains(idContainsText))
                                   .toList();
        if (filteredElements.isEmpty()) {
            throw new PlaywrightException(String.format("Unable to find element by child containing '%s' text", idContainsText));
        }

        if (filteredElements.size() > 1) {
            throw new PlaywrightException(String.format("%s elements found by '%s' text, but expected: 1", filteredElements.size(), idContainsText));
        }

        return filteredElements.getFirst();
    }
}
