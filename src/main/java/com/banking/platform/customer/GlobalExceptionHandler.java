package com.banking.platform.customer;

import com.banking.platform.customer.CustomerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(CustomerNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());        // 404
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, String>> handleDuplicate(DuplicateEmailException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());         // 409
    }

    @ExceptionHandler(InvalidCustomerStateException.class)
    public ResponseEntity<Map<String, String>> handleState(InvalidCustomerStateException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());         // 409
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadInput(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());      // 400
    }

    // Triggered when @Valid fails on a request DTO.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);  // 400 with field details
    }

    private ResponseEntity<Map<String, String>> build(HttpStatus status, String message) {
        Map<String, String> body = new HashMap<>();
        body.put("error", message);
        return ResponseEntity.status(status).body(body);
    }
}
