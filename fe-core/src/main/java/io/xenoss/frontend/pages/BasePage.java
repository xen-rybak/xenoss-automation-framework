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
        // Escape single quotes to prevent XPath injection
        String escapedTestId = escapeXPathString(testId);
        return getElement("//" + tagName + "[@data-testid=" + escapedTestId + "]");
    }

    public Locator getElementByText(String tagName, String text) {
        // Escape single quotes to prevent XPath injection
        String escapedText = escapeXPathString(text);
        return getElement("//" + tagName + "[contains(text(), " + escapedText + ")]");
    }

    public Locator getElementByContainsId(String tagName, String text) {
        // Escape single quotes to prevent XPath injection
        String escapedText = escapeXPathString(text);
        return getElement("//" + tagName + "[contains(@id, " + escapedText + ")]");
    }

    /**
     * Escapes an XPath string to prevent injection.
     * Handles strings containing both single and double quotes by using concat().
     *
     * @param value the string to escape
     * @return properly escaped XPath string
     */
    private String escapeXPathString(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        } else if (!value.contains("\"")) {
            return "\"" + value + "\"";
        } else {
            // String contains both quotes, use concat()
            StringBuilder result = new StringBuilder("concat(");
            String[] parts = value.split("'");
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) {
                    result.append(", \"'\", ");
                }
                if (!parts[i].isEmpty()) {
                    result.append("'").append(parts[i]).append("'");
                }
            }
            if (value.endsWith("'")) {
                result.append(", \"'\"");
            }
            result.append(")");
            return result.toString();
        }
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
