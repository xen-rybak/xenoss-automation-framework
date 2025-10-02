package io.xenoss.config;

import lombok.Getter;

@Getter
public class EnvironmentConfig {
    String customerSiteUrl;
    String missionControlUrl;
    String dspUrl;
    String bidderUrl;
    String reporterUrl;
    String cdnUrl;
    String audienceUrl;
    String internalEventTrackerUrl;
    String externalEventTrackerUrl;
    String organizationId;
    String accountId;
    String defaultUser;
    String defaultPassword;
    String smtpServerRestApiUrl;
    String smtpServerUserName;
    String smtpServerPassword;
}
