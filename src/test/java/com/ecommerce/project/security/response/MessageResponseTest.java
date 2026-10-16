package com.ecommerce.project.security.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageResponseTest {

    @Test
    void noArgsConstructorShouldInitializeMessageToNull() {
        MessageResponse response = new MessageResponse();

        assertNull(response.getMessage());
    }

    @Test
    void allArgsConstructorShouldInitializeMessage() {
        MessageResponse response = new MessageResponse("Operation successful");

        assertEquals("Operation successful", response.getMessage());
    }

    @Test
    void setterShouldUpdateMessage() {
        MessageResponse response = new MessageResponse();

        response.setMessage("Updated message");

        assertEquals("Updated message", response.getMessage());
    }

    @Test
    void equalsAndHashCodeShouldDependOnMessage() {
        MessageResponse first = new MessageResponse("Same message");
        MessageResponse second = new MessageResponse("Same message");
        MessageResponse different = new MessageResponse("Different message");

        assertAll(
                () -> assertEquals(first, second),
                () -> assertEquals(first.hashCode(), second.hashCode()),
                () -> assertNotEquals(first, different),
                () -> assertNotEquals(first, null),
                () -> assertNotEquals(first, "Same message")
        );
    }

    @Test
    void toStringShouldContainMessageValue() {
        MessageResponse response = new MessageResponse("Operation successful");

        assertTrue(response.toString().contains("Operation successful"));
    }
}
