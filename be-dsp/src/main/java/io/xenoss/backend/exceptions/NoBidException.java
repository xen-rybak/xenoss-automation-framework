package io.xenoss.backend.exceptions;

import org.testng.TestException;

public class NoBidException extends TestException {
    public NoBidException(String message) {
        super(message);
    }
}
