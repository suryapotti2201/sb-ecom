package com.ecommerce.project.security.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginRequestTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void noArgsConstructorAndSettersShouldPopulateFields() {
        LoginRequest request = new LoginRequest();

        request.setUsername("alice");
        request.setPassword("secret");

        assertEquals("alice", request.getUsername());
        assertEquals("secret", request.getPassword());
    }

    @Test
    void allArgsConstructorShouldPopulateFields() {
        LoginRequest request = new LoginRequest("alice", "secret");

        assertEquals("alice", request.getUsername());
        assertEquals("secret", request.getPassword());
    }

    @Test
    void validRequestShouldHaveNoValidationViolations() {
        LoginRequest request = new LoginRequest("alice", "secret");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void nullFieldsShouldProduceRequiredFieldViolations() {
        LoginRequest request = new LoginRequest(null, null);

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("username")
                        && violation.getMessage().equals("Username is required")));
        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("password")
                        && violation.getMessage().equals("Password is required")));
    }

    @Test
    void blankFieldsShouldProduceRequiredFieldViolations() {
        LoginRequest request = new LoginRequest("   ", "");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("username")
                        && violation.getMessage().equals("Username is required")));
        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("password")
                        && violation.getMessage().equals("Password is required")));
    }

    @Test
    void equalRequestsShouldBeEqualAndHaveSameHashCode() {
        LoginRequest first = new LoginRequest("alice", "secret");
        LoginRequest second = new LoginRequest("alice", "secret");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void requestsWithDifferentFieldsShouldNotBeEqual() {
        LoginRequest first = new LoginRequest("alice", "secret");
        LoginRequest second = new LoginRequest("bob", "secret");

        assertNotEquals(first, second);
    }

    @Test
    void toStringShouldContainFieldValues() {
        LoginRequest request = new LoginRequest("alice", "secret");

        String representation = request.toString();

        assertTrue(representation.contains("alice"));
        assertTrue(representation.contains("secret"));
        assertFalse(representation.isBlank());
    }
}
