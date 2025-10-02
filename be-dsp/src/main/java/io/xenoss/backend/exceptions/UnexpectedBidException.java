package io.xenoss.backend.exceptions;

import org.testng.TestException;

public class UnexpectedBidException extends TestException {
    public UnexpectedBidException(String message) {
        super(message);
    }
}
