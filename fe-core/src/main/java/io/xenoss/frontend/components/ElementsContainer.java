package io.xenoss.frontend.components;

import com.microsoft.playwright.Locator;
import io.xenoss.frontend.elements.ContainElements;
import io.xenoss.frontend.pages.BasePage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public abstract class ElementsContainer implements ContainElements {
    protected final BasePage page;
    protected final Locator containerElement;

    public ElementsContainer(BasePage page, Locator containerElement) {
        this.page = page;
        this.containerElement = containerElement;
    }

    public Locator getElement(String selector) {
        log.info("Looking for element by selector: '{}'", selector);
        return containerElement.locator(selector);
    }

    public void click() {
        containerElement.click();
    }
}
