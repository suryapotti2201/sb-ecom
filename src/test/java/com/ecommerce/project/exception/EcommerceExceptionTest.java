package com.ecommerce.project.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

class EcommerceExceptionTest {

    @Test
    void noArgumentConstructorCreatesExceptionWithNoMessage() {
        EcommerceException exception = new EcommerceException();

        assertInstanceOf(RuntimeException.class, exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void messageConstructorPreservesMessage() {
        String message = "An ecommerce error occurred";

        EcommerceException exception = new EcommerceException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void messageConstructorAcceptsNullMessage() {
        EcommerceException exception = new EcommerceException(null);

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void messageConstructorPreservesEmptyMessage() {
        EcommerceException exception = new EcommerceException("");

        assertEquals("", exception.getMessage());
    }
}
