package com.example.difyintegration.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Order
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleWebClientResponseException(WebClientResponseException ex) {
        log.error("WebClient error occurred: {} {} - Response: {}", 
                  ex.getStatusCode(), ex.getStatusText(), ex.getResponseBodyAsString());
        
        String errorMessage = String.format("Dify API Error: %d %s - %s", 
                                           ex.getStatusCode().value(), 
                                           ex.getStatusText(), 
                                           ex.getResponseBodyAsString());
        
        return ResponseEntity.status(ex.getStatusCode()).body(errorMessage);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        log.error("Response status error occurred: {}", ex.getMessage());
        
        return ResponseEntity.status(ex.getStatusCode()).body("Error: " + ex.getReason());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred", ex);
        
        String errorMessage = "An unexpected error occurred: " + ex.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }
}