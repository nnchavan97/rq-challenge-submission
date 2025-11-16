package com.reliaquest.api.exception.handler;

import com.reliaquest.api.dto.CustomErrorDto;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.EmployeeServiceIntegrationException;
import com.reliaquest.api.exception.TooManyRequestsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author nikhilchavan
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(EmployeeServiceIntegrationException.class)
    public ResponseEntity<CustomErrorDto> handleEmployeeServiceIntegrationException(
            EmployeeServiceIntegrationException ex) {

        CustomErrorDto errorDto = new CustomErrorDto();
        errorDto.setError(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<CustomErrorDto> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {

        CustomErrorDto errorDto = new CustomErrorDto();
        errorDto.setError(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<CustomErrorDto> handleTooManyRequestsException(TooManyRequestsException ex) {

        CustomErrorDto errorDto = new CustomErrorDto();
        errorDto.setError(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        CustomErrorDto errorDto = new CustomErrorDto();
        StringBuilder completeErrorMessage = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            completeErrorMessage
                    .append("Field : ")
                    .append(fieldName)
                    .append(" - ")
                    .append(errorMessage)
                    .append("; ");
        });
        errorDto.setError(completeErrorMessage.toString());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomErrorDto> handleEmployeeNotFoundException(IllegalArgumentException ex) {
        CustomErrorDto apiError = new CustomErrorDto();
        apiError.setError(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorDto> handleEmployeeServiceIntegrationException(Exception ex) {

        CustomErrorDto errorDto = new CustomErrorDto();
        errorDto.setError("An internal error occured. Please contact support@reliaquest.com");
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
