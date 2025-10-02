package io.xenoss.backend.client;

import io.xenoss.backend.model.smtp4dev.SmtpResponse;
import io.xenoss.backend.model.smtp4dev.SmtpResult;
import io.xenoss.config.ConfigurationManager;
import io.xenoss.config.EnvironmentConfig;
import io.xenoss.http.Response;
import io.xenoss.utils.ActionTimer;
import org.testng.TestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseSmtp4DevClient extends BaseClient {
    // Static final constants
    private static final int DEFAULT_TIMEOUT = 120; //seconds
    private static final String MESSAGES_PATH = "Messages";
    private static final String MESSAGE_BODY_PATH = "Messages/%s/raw";
    private static final EnvironmentConfig ENVIRONMENT_CONFIG = ConfigurationManager.getConfig()
                                                                                    .getEnvironmentConfig();

    // Constructors
    public BaseSmtp4DevClient() {
        super(ENVIRONMENT_CONFIG.getSmtpServerRestApiUrl(), ENVIRONMENT_CONFIG.getSmtpServerUserName(), ENVIRONMENT_CONFIG.getSmtpServerPassword());
    }

    public Response getMessages(String deliverToEmail) {
        return getMessages(deliverToEmail, "Default");
    }

    public Response getMessages(String deliverToEmail, String mailboxName) {
        return get(MESSAGES_PATH, Map.of(
                "mailboxName", new Object[]{mailboxName},
                "searchTerms", new Object[]{deliverToEmail},
                "sortColumn", new Object[]{"receivedDate"},
                "sortIsDescending", new Object[]{true},
                "page", new Object[]{1},
                "pageSize", new Object[]{500}));
    }

    public Response getMessageBody(String id) {
        return get(String.format(MESSAGE_BODY_PATH, id));
    }

    public List<SmtpResult> waitForMessage(String to, String subjectContains) {
        final List<SmtpResult> messages = new ArrayList<>();

        if (!ActionTimer.waitFor(() -> {
            messages.addAll(getMessages(to).as(SmtpResponse.class)
                                           .getResults()
                                           .stream()
                                           .filter(msg -> msg.getSubject()
                                                             .toLowerCase()
                                                             .contains(subjectContains.toLowerCase()))
                                           .toList());
            return !messages.isEmpty();
        }, DEFAULT_TIMEOUT)) {
            throw new TestException(String.format("Message to %s has not been found for %s seconds", to, DEFAULT_TIMEOUT));
        }

        return messages;
    }
}
