package com.ecommerce.project.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ResourceNotFoundExceptionTest {

    @Test
    void stringFieldConstructor_shouldSetFieldsAndFormatMessage() {
        ResourceNotFoundException exception =
                new ResourceNotFoundException("Product", "name", "Laptop");

        assertAll(
                () -> assertEquals("Product not found with name: Laptop", exception.getMessage()),
                () -> assertEquals("Product", exception.resourceName),
                () -> assertEquals("name", exception.field),
                () -> assertEquals("Laptop", exception.fieldName),
                () -> assertNull(exception.fieldId)
        );
    }

    @Test
    void idFieldConstructor_shouldSetFieldsAndFormatMessage() {
        ResourceNotFoundException exception =
                new ResourceNotFoundException("Product", "id", 42L);

        assertAll(
                () -> assertEquals("Product not found with id: 42", exception.getMessage()),
                () -> assertEquals("Product", exception.resourceName),
                () -> assertEquals("id", exception.field),
                () -> assertNull(exception.fieldName),
                () -> assertEquals(42L, exception.fieldId)
        );
    }

    @Test
    void stringFieldConstructor_shouldSupportNullFieldName() {
        ResourceNotFoundException exception =
                new ResourceNotFoundException("Product", "name", (String) null);

        assertAll(
                () -> assertEquals("Product not found with name: null", exception.getMessage()),
                () -> assertEquals("Product", exception.resourceName),
                () -> assertEquals("name", exception.field),
                () -> assertNull(exception.fieldName),
                () -> assertNull(exception.fieldId)
        );
    }

    @Test
    void idFieldConstructor_shouldSupportNullFieldId() {
        ResourceNotFoundException exception =
                new ResourceNotFoundException("Product", "id", (Long) null);

        assertAll(
                () -> assertEquals("Product not found with id: null", exception.getMessage()),
                () -> assertEquals("Product", exception.resourceName),
                () -> assertEquals("id", exception.field),
                () -> assertNull(exception.fieldName),
                () -> assertNull(exception.fieldId)
        );
    }
}
