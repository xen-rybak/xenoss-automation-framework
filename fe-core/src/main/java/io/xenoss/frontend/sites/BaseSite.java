package io.xenoss.frontend.sites;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;

public abstract class BaseSite {
    protected final Page playwrightPage;
    protected final String homePageUrl;

    public BaseSite(Page playwrightPage, String homePageUrl) {
        if (playwrightPage == null) {
            throw new IllegalArgumentException("playwrightPage cannot be null");
        }
        if (homePageUrl == null || homePageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("homePageUrl cannot be null or empty");
        }
        this.playwrightPage = playwrightPage;
        this.homePageUrl = homePageUrl;
    }

    public void navigateHomePage() {
        playwrightPage.navigate(homePageUrl,
                new Page.NavigateOptions()
                        .setTimeout(60000)
                        .setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    }

}
