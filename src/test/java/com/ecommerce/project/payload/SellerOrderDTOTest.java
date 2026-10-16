package com.ecommerce.project.payload;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SellerOrderDTOTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDownValidator() {
        validatorFactory.close();
    }

    @Test
    void noArgsConstructorShouldInitializeFieldsToNull() {
        SellerOrderDTO dto = new SellerOrderDTO();

        assertEquals(null, dto.getOrderStatus());
        assertEquals(null, dto.getRemark());
    }

    @Test
    void allArgsConstructorShouldInitializeFields() {
        SellerOrderDTO dto = new SellerOrderDTO("PACKED", "Ready for dispatch");

        assertEquals("PACKED", dto.getOrderStatus());
        assertEquals("Ready for dispatch", dto.getRemark());
    }

    @Test
    void settersShouldUpdateFields() {
        SellerOrderDTO dto = new SellerOrderDTO();

        dto.setOrderStatus("DELIVERED");
        dto.setRemark("Delivered successfully");

        assertEquals("DELIVERED", dto.getOrderStatus());
        assertEquals("Delivered successfully", dto.getRemark());
    }

    @ParameterizedTest
    @ValueSource(strings = {"PACKED", "IN_TRANSIT", "OUT_FOR_DELIVERY", "DELIVERED"})
    void supportedOrderStatusesShouldBeValid(String orderStatus) {
        SellerOrderDTO dto = new SellerOrderDTO(orderStatus, "Order update");

        Set<ConstraintViolation<SellerOrderDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void nullOrderStatusShouldBeInvalid() {
        SellerOrderDTO dto = new SellerOrderDTO(null, null);

        Set<ConstraintViolation<SellerOrderDTO>> violations = validator.validate(dto);

        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("orderStatus")));
    }

    @Test
    void blankOrderStatusShouldBeInvalid() {
        SellerOrderDTO dto = new SellerOrderDTO("   ", null);

        Set<ConstraintViolation<SellerOrderDTO>> violations = validator.validate(dto);

        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("orderStatus")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"PACKED ", "packed", "IN TRANSIT", "CANCELLED", "DELIVERED_NOW"})
    void unsupportedOrderStatusShouldBeInvalid(String orderStatus) {
        SellerOrderDTO dto = new SellerOrderDTO(orderStatus, null);

        Set<ConstraintViolation<SellerOrderDTO>> violations = validator.validate(dto);

        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("orderStatus")));
    }

    @Test
    void remarkAtMaximumLengthShouldBeValid() {
        SellerOrderDTO dto = new SellerOrderDTO("PACKED", "a".repeat(500));

        Set<ConstraintViolation<SellerOrderDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void remarkLongerThanMaximumShouldBeInvalid() {
        SellerOrderDTO dto = new SellerOrderDTO("PACKED", "a".repeat(501));

        Set<ConstraintViolation<SellerOrderDTO>> violations = validator.validate(dto);

        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("remark")));
    }

    @Test
    void nullRemarkShouldBeValidWhenOrderStatusIsValid() {
        SellerOrderDTO dto = new SellerOrderDTO("DELIVERED", null);

        Set<ConstraintViolation<SellerOrderDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void equalObjectsShouldBeEqualAndHaveSameHashCode() {
        SellerOrderDTO first = new SellerOrderDTO("IN_TRANSIT", "On the way");
        SellerOrderDTO second = new SellerOrderDTO("IN_TRANSIT", "On the way");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void objectsWithDifferentValuesShouldNotBeEqual() {
        SellerOrderDTO first = new SellerOrderDTO("PACKED", "Preparing");
        SellerOrderDTO second = new SellerOrderDTO("DELIVERED", "Completed");

        assertNotEquals(first, second);
    }

    @Test
    void toStringShouldContainFieldValues() {
        SellerOrderDTO dto = new SellerOrderDTO("OUT_FOR_DELIVERY", "Courier dispatched");

        String result = dto.toString();

        assertTrue(result.contains("OUT_FOR_DELIVERY"));
        assertTrue(result.contains("Courier dispatched"));
    }

    @Test
    void objectShouldNotEqualNullOrObjectOfDifferentType() {
        SellerOrderDTO dto = new SellerOrderDTO("PACKED", "Preparing");

        assertNotEquals(null, dto);
        assertFalse(dto.equals(new Object()));
    }
}
