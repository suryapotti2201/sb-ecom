package com.ecommerce.project.security.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignupRequestTest {

    private ValidatorFactory validatorFactory;
    private Validator validator;

    @BeforeEach
    void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterEach
    void tearDown() {
        validatorFactory.close();
    }

    @Test
    void noArgsConstructorShouldCreateEmptyRequest() {
        SignupRequest request = new SignupRequest();

        assertEquals(null, request.getUserName());
        assertEquals(null, request.getEmailId());
        assertEquals(null, request.getPassword());
    }

    @Test
    void allArgsConstructorShouldPopulateAllFields() {
        SignupRequest request =
                new SignupRequest("johndoe", "john.doe@example.com", "secret");

        assertEquals("johndoe", request.getUserName());
        assertEquals("john.doe@example.com", request.getEmailId());
        assertEquals("secret", request.getPassword());
    }

    @Test
    void settersShouldUpdateAllFields() {
        SignupRequest request = new SignupRequest();

        request.setUserName("janedoe");
        request.setEmailId("jane.doe@example.com");
        request.setPassword("password");

        assertEquals("janedoe", request.getUserName());
        assertEquals("jane.doe@example.com", request.getEmailId());
        assertEquals("password", request.getPassword());
    }

    @Test
    void validRequestShouldHaveNoConstraintViolations() {
        SignupRequest request =
                new SignupRequest("abc", "a@b.com", "abc");

        Set<ConstraintViolation<SignupRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void validBoundaryValuesShouldHaveNoConstraintViolations() {
        SignupRequest request =
                new SignupRequest("abcdefghijklmnopqrst", "a@b.com", "abcdefghij");

        Set<ConstraintViolation<SignupRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void blankFieldsShouldBeRejected() {
        SignupRequest request = new SignupRequest("", " ", null);

        Set<ConstraintViolation<SignupRequest>> violations =
                validator.validate(request);

        assertTrue(hasViolationFor(violations, "userName"));
        assertTrue(hasViolationFor(violations, "emailId"));
        assertTrue(hasViolationFor(violations, "password"));
    }

    @Test
    void usernameOutsideAllowedLengthShouldBeRejected() {
        SignupRequest tooShort = new SignupRequest("ab", "a@b.com", "abc");
        SignupRequest tooLong =
                new SignupRequest("abcdefghijklmnopqrstu", "a@b.com", "abc");

        assertTrue(hasViolationFor(validator.validate(tooShort), "userName"));
        assertTrue(hasViolationFor(validator.validate(tooLong), "userName"));
    }

    @Test
    void invalidEmailOrEmailLongerThanAllowedShouldBeRejected() {
        SignupRequest invalidEmail =
                new SignupRequest("username", "not-an-email", "abc");
        SignupRequest tooLongEmail =
                new SignupRequest(
                        "username",
                        "a".repeat(40) + "@example.com",
                        "abc");

        assertTrue(hasViolationFor(validator.validate(invalidEmail), "emailId"));
        assertTrue(hasViolationFor(validator.validate(tooLongEmail), "emailId"));
    }

    @Test
    void passwordOutsideAllowedLengthShouldBeRejected() {
        SignupRequest tooShort =
                new SignupRequest("username", "a@b.com", "ab");
        SignupRequest tooLong =
                new SignupRequest("username", "a@b.com", "abcdefghijk");

        assertTrue(hasViolationFor(validator.validate(tooShort), "password"));
        assertTrue(hasViolationFor(validator.validate(tooLong), "password"));
    }

    @Test
    void equalRequestsShouldBeEqualAndHaveSameHashCode() {
        SignupRequest first =
                new SignupRequest("username", "user@example.com", "secret");
        SignupRequest second =
                new SignupRequest("username", "user@example.com", "secret");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void requestsWithDifferentValuesShouldNotBeEqual() {
        SignupRequest first =
                new SignupRequest("username", "user@example.com", "secret");
        SignupRequest second =
                new SignupRequest("different", "user@example.com", "secret");

        assertNotEquals(first, second);
    }

    private boolean hasViolationFor(
            Set<ConstraintViolation<SignupRequest>> violations,
            String propertyName) {
        return violations.stream()
                .anyMatch(violation ->
                        violation.getPropertyPath().toString().equals(propertyName));
    }
}
