package io.xenoss.backend.client;

import io.xenoss.http.Header;
import lombok.Getter;


public abstract class BaseDspApp {
    @Getter
    private final String organizationId;
    @Getter
    private final Header authHeader;
    @Getter
    private final String userEmail;

    // Constructors
    public BaseDspApp(String email, String organizationId, Header authHeader) {
        this.authHeader = authHeader;
        this.organizationId = organizationId;
        this.userEmail = email;
    }
}
