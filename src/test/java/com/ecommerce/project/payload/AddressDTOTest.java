package com.ecommerce.project.payload;

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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddressDTOTest {

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
    void noArgsConstructorShouldInitializeFieldsToNull() {
        AddressDTO address = new AddressDTO();

        assertNull(address.getStreet());
        assertNull(address.getBuildingName());
        assertNull(address.getCity());
        assertNull(address.getCountry());
        assertNull(address.getPincode());
    }

    @Test
    void allArgsConstructorAndAccessorsShouldExposeAddressValues() {
        AddressDTO address = new AddressDTO(
                "Main Street",
                "Sunrise Apartments",
                "Mumbai",
                "India",
                "400001"
        );

        assertEquals("Main Street", address.getStreet());
        assertEquals("Sunrise Apartments", address.getBuildingName());
        assertEquals("Mumbai", address.getCity());
        assertEquals("India", address.getCountry());
        assertEquals("400001", address.getPincode());

        address.setStreet("Park Street");
        address.setBuildingName("Central Towers");
        address.setCity("Kolkata");
        address.setCountry("India");
        address.setPincode("700001");

        assertEquals("Park Street", address.getStreet());
        assertEquals("Central Towers", address.getBuildingName());
        assertEquals("Kolkata", address.getCity());
        assertEquals("India", address.getCountry());
        assertEquals("700001", address.getPincode());
    }

    @Test
    void equalAddressesShouldBeEqualAndHaveSameHashCode() {
        AddressDTO first = validAddress();
        AddressDTO second = new AddressDTO(
                "Main Street",
                "Sunrise Apartments",
                "Mumbai",
                "India",
                "400001"
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(first.toString(), second.toString());
    }

    @Test
    void validAddressShouldHaveNoConstraintViolations() {
        Set<ConstraintViolation<AddressDTO>> violations =
                validator.validate(validAddress());

        assertTrue(violations.isEmpty());
    }

    @Test
    void missingRequiredFieldsShouldProduceViolations() {
        AddressDTO address = new AddressDTO(
                "",
                null,
                " ",
                "India",
                "400001"
        );

        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);

        assertTrue(hasViolationFor(violations, "street"));
        assertTrue(hasViolationFor(violations, "city"));
        assertFalse(hasViolationFor(violations, "country"));
        assertFalse(hasViolationFor(violations, "pincode"));
    }

    @Test
    void fieldsExceedingMaximumLengthsShouldProduceViolations() {
        AddressDTO address = new AddressDTO(
                repeated('s', 256),
                repeated('b', 151),
                repeated('c', 101),
                repeated('o', 61),
                "400001"
        );

        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);

        assertTrue(hasViolationFor(violations, "street"));
        assertTrue(hasViolationFor(violations, "buildingName"));
        assertTrue(hasViolationFor(violations, "city"));
        assertTrue(hasViolationFor(violations, "country"));
        assertFalse(hasViolationFor(violations, "pincode"));
    }

    @Test
    void nullBuildingNameShouldBeAllowed() {
        AddressDTO address = new AddressDTO(
                "Main Street",
                null,
                "Mumbai",
                "India",
                "400001"
        );

        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);

        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidPinCodesShouldProduceViolations() {
        assertTrue(hasViolationFor(
                validator.validate(addressWithPincode("012345")), "pincode"));
        assertTrue(hasViolationFor(
                validator.validate(addressWithPincode("12345")), "pincode"));
        assertTrue(hasViolationFor(
                validator.validate(addressWithPincode("1234567")), "pincode"));
        assertTrue(hasViolationFor(
                validator.validate(addressWithPincode("12345A")), "pincode"));
    }

    @Test
    void sixDigitPinCodeNotStartingWithZeroShouldBeValid() {
        AddressDTO address = addressWithPincode("123456");

        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);

        assertFalse(hasViolationFor(violations, "pincode"));
    }

    private static AddressDTO validAddress() {
        return new AddressDTO(
                "Main Street",
                "Sunrise Apartments",
                "Mumbai",
                "India",
                "400001"
        );
    }

    private static AddressDTO addressWithPincode(String pincode) {
        AddressDTO address = validAddress();
        address.setPincode(pincode);
        return address;
    }

    private static boolean hasViolationFor(
            Set<ConstraintViolation<AddressDTO>> violations,
            String property
    ) {
        return violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals(property));
    }

    private static String repeated(char character, int length) {
        return String.valueOf(character).repeat(length);
    }
}
