package es.codeurjc.web.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

// This class will handle exceptions thrown by only REST controller
@RestControllerAdvice(annotations = RestController.class)
public class GlobalRestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

    //needed to catch any unexpected exceptions and return a 500 Internal Server Error with a JSON body instead of a default HTML error page
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericExceptions(Exception ex) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", ex.getMessage()); 
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
