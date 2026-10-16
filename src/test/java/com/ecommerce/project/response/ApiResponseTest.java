package com.ecommerce.project.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiResponseTest {

    @Test
    void noArgsConstructorCreatesResponseWithDefaultValues() {
        ApiResponse response = new ApiResponse();

        assertNull(response.getMessage());
        assertFalse(response.isStatus());
    }

    @Test
    void allArgsConstructorInitializesMessageAndStatus() {
        ApiResponse response = new ApiResponse("Operation successful", true);

        assertEquals("Operation successful", response.getMessage());
        assertTrue(response.isStatus());
        assertEquals("Operation successful", response.message);
    }

    @Test
    void settersUpdateMessageAndStatus() {
        ApiResponse response = new ApiResponse();

        response.setMessage("Updated message");
        response.setStatus(true);

        assertEquals("Updated message", response.getMessage());
        assertEquals("Updated message", response.message);
        assertTrue(response.isStatus());

        response.setMessage(null);
        response.setStatus(false);

        assertNull(response.getMessage());
        assertFalse(response.isStatus());
    }

    @Test
    void equalResponsesAreEqualAndHaveSameHashCode() {
        ApiResponse first = new ApiResponse("Completed", true);
        ApiResponse second = new ApiResponse("Completed", true);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void responsesWithDifferentValuesAreNotEqual() {
        ApiResponse differentMessage = new ApiResponse("Failed", true);
        ApiResponse differentStatus = new ApiResponse("Completed", false);
        ApiResponse original = new ApiResponse("Completed", true);

        assertNotEquals(original, differentMessage);
        assertNotEquals(original, differentStatus);
        assertNotEquals(original, null);
        assertNotEquals(original, "Completed");
    }

    @Test
    void toStringContainsResponseState() {
        ApiResponse response = new ApiResponse("Completed", true);

        String representation = response.toString();

        assertTrue(representation.contains("Completed"));
        assertTrue(representation.contains("true"));
    }
}
