package io.xenoss.frontend.sites;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;

public abstract class BaseSite {
    protected final Page playwrightPage;
    protected final String homePageUrl;

    public BaseSite(Page playwrightPage, String homePageUrl) {
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
