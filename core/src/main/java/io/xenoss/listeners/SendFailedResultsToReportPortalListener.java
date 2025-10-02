package io.xenoss.listeners;

import com.epam.reportportal.listeners.LogLevel;
import com.epam.reportportal.service.ReportPortal;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Date;

public class SendFailedResultsToReportPortalListener implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result) {
        var testClassInstance = result.getMethod()
                                      .getInstance();
        if (testClassInstance instanceof Screenshotable) {
            ReportPortal.emitLog("Test failed", LogLevel.ERROR.name(), new Date(),
                    ((Screenshotable) testClassInstance).makeScreenshot());
        }
    }
}
