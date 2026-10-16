package com.ecommerce.project.exception;

import com.ecommerce.project.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomExceptionHandlerTest {

    private final CustomExceptionHandler handler = new CustomExceptionHandler();

    @Test
    void myMethodArgumentNotValidExceptionReturnsFieldMessagesAndBadRequest() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError nameError = new FieldError(
                "request",
                "name",
                "Name is required"
        );
        FieldError emailError = new FieldError(
                "request",
                "email",
                "Email is invalid"
        );

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(nameError, emailError));

        ResponseEntity<Map<String, String>> response =
                handler.myMethodArgumentNotValidException(exception);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(
                Map.of(
                        "name", "Name is required",
                        "email", "Email is invalid"
                ),
                response.getBody()
        );
        verify(exception).getBindingResult();
        verify(bindingResult).getAllErrors();
    }

    @Test
    void myMethodArgumentNotValidExceptionReturnsEmptyMapWhenThereAreNoErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of());

        ResponseEntity<Map<String, String>> response =
                handler.myMethodArgumentNotValidException(exception);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(Map.of(), response.getBody());
    }

    @Test
    void myResourceNotFoundExceptionReturnsMessageAndNotFound() {
        ResourceNotFoundException exception =
                new ResourceNotFoundException("Product", "id", 42L);

        ResponseEntity<ApiResponse> response =
                handler.myResourceNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(exception.getMessage(), response.getBody().getMessage());
    }

    @Test
    void myEcommerceExceptionReturnsMessageAndNotFound() {
        EcommerceException exception = new EcommerceException("Unable to process request");

        ResponseEntity<ApiResponse> response =
                handler.myEcommerceException(exception);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(exception.getMessage(), response.getBody().getMessage());
    }

    @Test
    void myEcommerceExceptionPreservesNullMessage() {
        EcommerceException exception = new EcommerceException();

        ResponseEntity<ApiResponse> response =
                handler.myEcommerceException(exception);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(null, response.getBody().getMessage());
    }
}
