package com.laurefindel.finance.exceptions;

import com.laurefindel.finance.dto.ErrorResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Min;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
    }

    @Test
    void handleNotFound_shouldReturn404() {
        ResponseEntity<ErrorResponseDto> response =
            handler.handleNotFound(new NoSuchElementException("Not found"), request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertBody(response, 404, "Not Found", "Not found", "/api/test");
        assertNull(response.getBody().getValidationErrors());
    }

    @Test
    void handleIllegalArgument_shouldReturn400() {
        ResponseEntity<ErrorResponseDto> response =
            handler.handleIllegalArgument(new IllegalArgumentException("Invalid input"), request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertBody(response, 400, "Bad Request", "Invalid input", "/api/test");
        assertNull(response.getBody().getValidationErrors());
    }

    @Test
    void handlePartialBulkOperation_shouldIncludeDetails() {
        PartialBulkOperationException ex = new PartialBulkOperationException(
            "Partial failure",
            2,
            1,
            Map.of("operation_3", "Insufficient funds")
        );

        ResponseEntity<ErrorResponseDto> response = handler.handlePartialBulkOperation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertBody(response, 400, "Bad Request", "Partial failure", "/api/test");
        assertEquals("2", response.getBody().getValidationErrors().get("saved"));
        assertEquals("1", response.getBody().getValidationErrors().get("failed"));
        assertEquals("Insufficient funds", response.getBody().getValidationErrors().get("operation_3"));
    }

    @Test
    void handleDataIntegrityViolation_shouldReturn409() {
        RuntimeException rootCause = new RuntimeException("duplicate key value violates unique constraint");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Integrity", rootCause);

        ResponseEntity<ErrorResponseDto> response = handler.handleDataIntegrityViolation(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertBody(response, 409, "Conflict", rootCause.getMessage(), "/api/test");
    }

    @Test
    void handleTypeMismatch_shouldReturn400WithParameterMessage() throws Exception {
        Method method = GlobalExceptionHandler.class.getDeclaredMethod(
            "handleIllegalArgument",
            IllegalArgumentException.class,
            HttpServletRequest.class
        );
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentTypeMismatchException ex =
            new MethodArgumentTypeMismatchException("abc", Integer.class, "id", parameter, null);

        ResponseEntity<ErrorResponseDto> response = handler.handleTypeMismatch(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertBody(response, 400, "Bad Request", "Invalid value for parameter 'id': abc", "/api/test");
    }

    @Test
    void handleMethodArgumentNotValid_shouldReturnValidationErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "amount", "must be positive"));

        Method method = GlobalExceptionHandler.class.getDeclaredMethod(
            "handleIllegalArgument",
            IllegalArgumentException.class,
            HttpServletRequest.class
        );
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponseDto> response = handler.handleMethodArgumentNotValid(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertBody(response, 400, "Bad Request", "Validation failed", "/api/test");
        assertEquals("must be positive", response.getBody().getValidationErrors().get("amount"));
    }

    @Test
    void handleBindException_shouldReturnValidationErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "currencyCode", "size must be 3"));
        BindException ex = new BindException(bindingResult);

        ResponseEntity<ErrorResponseDto> response = handler.handleBindException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertBody(response, 400, "Bad Request", "Validation failed", "/api/test");
        assertEquals("size must be 3", response.getBody().getValidationErrors().get("currencyCode"));
    }

    @Test
    void handleConstraintViolation_shouldReturnValidationErrors() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        InvalidConstraintPayload payload = new InvalidConstraintPayload(0);
        ConstraintViolationException ex = new ConstraintViolationException(validator.validate(payload));

        ResponseEntity<ErrorResponseDto> response = handler.handleConstraintViolation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertBody(response, 400, "Bad Request", "Validation failed", "/api/test");
        assertEquals("must be greater than or equal to 1", response.getBody().getValidationErrors().get("amount"));
    }

    @Test
    void handleUnexpectedException_shouldReturn500() {
        ResponseEntity<ErrorResponseDto> response =
            handler.handleUnexpectedException(new RuntimeException("boom"), request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertBody(response, 500, "Internal Server Error", "Unexpected server error", "/api/test");
        assertNull(response.getBody().getValidationErrors());
    }

    private static class InvalidConstraintPayload {
        @Min(1)
        private final int amount;

        private InvalidConstraintPayload(int amount) {
            this.amount = amount;
        }
    }

    private static void assertBody(
        ResponseEntity<ErrorResponseDto> response,
        int status,
        String error,
        String message,
        String path
    ) {
        ErrorResponseDto body = response.getBody();
        assertNotNull(body);
        assertEquals(status, body.getStatus());
        assertEquals(error, body.getError());
        assertEquals(message, body.getMessage());
        assertEquals(path, body.getPath());
        assertNotNull(body.getTimestamp());
    }
}
