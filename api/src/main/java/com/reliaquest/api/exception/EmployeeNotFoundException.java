package com.reliaquest.api.exception;

/**
 * @author nikhilchavan
 */
public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
