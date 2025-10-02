package io.xenoss.frontend.elements;

import com.microsoft.playwright.Locator;
import io.xenoss.config.ConfigurationManager;
import io.xenoss.frontend.utils.ArrayUtils;
import io.xenoss.utils.ActionTimer;
import io.xenoss.utils.WaitUtils;
import lombok.SneakyThrows;

public interface ContainElements {
    Locator getElement(String selector);

    @SneakyThrows
    default LocatorsList getElements(String selector) {
        var locator = getElement(selector);
        var timer = ActionTimer.start(ConfigurationManager.getConfig()
                                                          .getUiActionTimeoutSeconds());

        while (!timer.isExpired()) {
            if (!locator.all()
                        .isEmpty()) {
                return locator.all()
                              .stream()
                              .collect(ArrayUtils.toLocatorsList());
            }
            WaitUtils.forSeconds(1);
        }

        return new LocatorsList();
    }
}
