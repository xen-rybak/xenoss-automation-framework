package io.xenoss.config;

import lombok.Getter;

/**
 * Environment-specific configuration settings.
 * Contains URLs, credentials, and other environment-dependent values.
 */
@Getter
public class EnvironmentConfig {
    private String customerSiteUrl;
    private String missionControlUrl;
    private String dspUrl;
    private String bidderUrl;
    private String reporterUrl;
    private String cdnUrl;
    private String audienceUrl;
    private String internalEventTrackerUrl;
    private String externalEventTrackerUrl;
    private String organizationId;
    private String accountId;
    private String defaultUser;
    private String defaultPassword;
    private String smtpServerRestApiUrl;
    private String smtpServerUserName;
    private String smtpServerPassword;
}
