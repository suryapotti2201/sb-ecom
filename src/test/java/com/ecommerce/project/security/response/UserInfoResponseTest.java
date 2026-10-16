package com.ecommerce.project.security.response;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserInfoResponseTest {

    @Test
    void noArgsConstructorCreatesResponseWithNullFields() {
        UserInfoResponse response = new UserInfoResponse();

        assertNull(response.getId());
        assertNull(response.getJwtToken());
        assertNull(response.getUsername());
        assertNull(response.getRoles());
    }

    @Test
    void allArgsConstructorInitializesAllFields() {
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");

        UserInfoResponse response = new UserInfoResponse(
                42L,
                "jwt-token",
                "jane.doe",
                roles
        );

        assertEquals(42L, response.getId());
        assertEquals("jwt-token", response.getJwtToken());
        assertEquals("jane.doe", response.getUsername());
        assertSame(roles, response.getRoles());
    }

    @Test
    void settersUpdateAllFields() {
        UserInfoResponse response = new UserInfoResponse();
        List<String> roles = List.of("ROLE_USER");

        response.setId(7L);
        response.setJwtToken("updated-token");
        response.setUsername("updated.user");
        response.setRoles(roles);

        assertEquals(7L, response.getId());
        assertEquals("updated-token", response.getJwtToken());
        assertEquals("updated.user", response.getUsername());
        assertSame(roles, response.getRoles());
    }

    @Test
    void equalsAndHashCodeMatchForResponsesWithSameValues() {
        List<String> roles = List.of("ROLE_USER");
        UserInfoResponse first = new UserInfoResponse(1L, "token", "user", roles);
        UserInfoResponse second = new UserInfoResponse(
                1L,
                "token",
                "user",
                List.of("ROLE_USER")
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void equalsDistinguishesResponsesWithDifferentValues() {
        UserInfoResponse first = new UserInfoResponse(
                1L,
                "token",
                "user",
                List.of("ROLE_USER")
        );
        UserInfoResponse second = new UserInfoResponse(
                2L,
                "token",
                "user",
                List.of("ROLE_USER")
        );

        assertNotEquals(first, second);
    }

    @Test
    void toStringContainsResponseFieldValues() {
        UserInfoResponse response = new UserInfoResponse(
                42L,
                "jwt-token",
                "jane.doe",
                List.of("ROLE_USER")
        );

        String representation = response.toString();

        assertTrue(representation.contains("42"));
        assertTrue(representation.contains("jwt-token"));
        assertTrue(representation.contains("jane.doe"));
        assertTrue(representation.contains("ROLE_USER"));
    }
}
