package io.xenoss.backend.client;

import io.xenoss.http.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomClient extends BaseClient {

    public CustomClient() {
        super("");
    }

    public Response get(String url) {
        return super.get(url);
    }
}
