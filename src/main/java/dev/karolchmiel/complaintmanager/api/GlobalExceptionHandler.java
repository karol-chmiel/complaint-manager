package dev.karolchmiel.complaintmanager.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation errors from @Valid annotations.
     * Returns a 400 Bad Request response with details about the validation errors.
     *
     * @param ex the exception thrown when validation fails
     * @return a map of field names to error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        LOG.warn("Validation error occurred: {}", ex.getMessage());

        final var errors = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            final var fieldName = ((FieldError) error).getField();
            final var errorMessage = error.getDefaultMessage();
            LOG.debug("Validation error on field '{}': {}", fieldName, errorMessage);
            errors.put(fieldName, errorMessage);
        });

        LOG.info("Returning BAD_REQUEST with {} validation errors", errors.size());
        return ResponseEntity.badRequest().body(errors);
    }
}
