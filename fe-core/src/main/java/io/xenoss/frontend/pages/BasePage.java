package io.xenoss.frontend.pages;

import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.xenoss.frontend.elements.ContainElements;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BasePage implements ContainElements {
    protected final Page playwrightPage;

    public BasePage(Page playwrightPage) {
        this.playwrightPage = playwrightPage;
    }

    public Locator getElement(String selector) {
        return playwrightPage.locator(selector);
    }

    public Locator getElementByTestId(String tagName, String testId) {
        return getElement("//" + tagName + "[@data-testid='" + testId + "']");
    }

    public Locator getElementByText(String tagName, String text) {
        return getElement("//" + tagName + "[contains(text(), '" + text + "')]");
    }

    public Locator getElementByContainsId(String tagName, String text) {
        return getElement("//" + tagName + "[contains(@id, '" + text + "')]");
    }

    public FileChooser getFileChooser(Runnable callback) {
        return playwrightPage.waitForFileChooser(callback);
    }

    public Locator getElementByTestIdv2(String tagName, String testId) {
        return getElement("//" + tagName + "[@id='" + testId + "']");
    }

    public void selectDropdown(String Name) {
        Locator option = getElementByText("li", Name);
        option.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        option.click();
    }

    public void addInterceptor(String urlPattern, String mockedJsonResponse) {
        playwrightPage.route(urlPattern, route -> {
            log.info("Intercepted request: {}", urlPattern);
            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200)
                    .setContentType("application/json")
                    .setBody(mockedJsonResponse)
            );
        });
    }
}
