package io.xenoss.testdata;

import io.xenoss.backend.model.error.ErrorResponse;

import java.util.List;

public class ErrorData {
    public ErrorResponse getErrorResponse(String... errors) {
        return ErrorResponse.builder()
                            .errors(List.of(errors))
                            .build();
    }
}
