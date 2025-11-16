package com.reliaquest.api.exception;

/**
 * @author nikhilchavan
 */
public class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException(String message) {
        super(message);
    }
}
