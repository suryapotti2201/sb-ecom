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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderRequestDTOTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void noArgsConstructorShouldInitializeFieldsToNull() {
        OrderRequestDTO dto = new OrderRequestDTO();

        assertNull(dto.getAddressId());
        assertNull(dto.getPaymentType());
        assertNull(dto.getPgPaymentId());
        assertNull(dto.getPgStatus());
        assertNull(dto.getPgResponseMessage());
        assertNull(dto.getPgName());
    }

    @Test
    void allArgsConstructorShouldInitializeAllFields() {
        OrderRequestDTO dto = new OrderRequestDTO(
                42L,
                "CARD",
                "payment-123",
                "SUCCESS",
                "Payment completed",
                "Stripe"
        );

        assertEquals(42L, dto.getAddressId());
        assertEquals("CARD", dto.getPaymentType());
        assertEquals("payment-123", dto.getPgPaymentId());
        assertEquals("SUCCESS", dto.getPgStatus());
        assertEquals("Payment completed", dto.getPgResponseMessage());
        assertEquals("Stripe", dto.getPgName());
    }

    @Test
    void settersShouldUpdateAllFields() {
        OrderRequestDTO dto = new OrderRequestDTO();

        dto.setAddressId(10L);
        dto.setPaymentType("UPI");
        dto.setPgPaymentId("gateway-payment-id");
        dto.setPgStatus("PENDING");
        dto.setPgResponseMessage("Awaiting confirmation");
        dto.setPgName("PayPal");

        assertEquals(10L, dto.getAddressId());
        assertEquals("UPI", dto.getPaymentType());
        assertEquals("gateway-payment-id", dto.getPgPaymentId());
        assertEquals("PENDING", dto.getPgStatus());
        assertEquals("Awaiting confirmation", dto.getPgResponseMessage());
        assertEquals("PayPal", dto.getPgName());
    }

    @Test
    void validOrderRequestShouldHaveNoConstraintViolations() {
        OrderRequestDTO dto = new OrderRequestDTO(
                1L,
                "C".repeat(50),
                "P".repeat(255),
                "SUCCESS",
                "Approved",
                "G".repeat(100)
        );

        Set<ConstraintViolation<OrderRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void nullAndNonPositiveAddressIdShouldBeRejected() {
        OrderRequestDTO nullAddress = new OrderRequestDTO(
                null, "CARD", null, null, null, null
        );
        OrderRequestDTO zeroAddress = new OrderRequestDTO(
                0L, "CARD", null, null, null, null
        );
        OrderRequestDTO negativeAddress = new OrderRequestDTO(
                -1L, "CARD", null, null, null, null
        );

        assertTrue(hasViolation(nullAddress, "addressId", "Address ID is required"));
        assertTrue(hasViolation(zeroAddress, "addressId", "Address ID must be greater than zero"));
        assertTrue(hasViolation(negativeAddress, "addressId", "Address ID must be greater than zero"));
    }

    @Test
    void blankPaymentTypeShouldBeRejected() {
        OrderRequestDTO nullPaymentType = new OrderRequestDTO(
                1L, null, null, null, null, null
        );
        OrderRequestDTO blankPaymentType = new OrderRequestDTO(
                1L, "   ", null, null, null, null
        );

        assertTrue(hasViolation(nullPaymentType, "paymentType", "Payment type is required"));
        assertTrue(hasViolation(blankPaymentType, "paymentType", "Payment type is required"));
    }

    @Test
    void oversizedPaymentFieldsShouldBeRejected() {
        OrderRequestDTO dto = new OrderRequestDTO(
                1L,
                "P".repeat(51),
                "I".repeat(256),
                null,
                null,
                "N".repeat(101)
        );

        Set<ConstraintViolation<OrderRequestDTO>> violations = validator.validate(dto);

        assertTrue(hasViolation(dto, "paymentType",
                "Payment type must not exceed 50 characters"));
        assertTrue(hasViolation(dto, "pgPaymentId",
                "Payment gateway payment ID must not exceed 255 characters"));
        assertTrue(hasViolation(dto, "pgName",
                "Payment gateway name must not exceed 100 characters"));
        assertEquals(3, violations.size());
    }

    @Test
    void optionalPaymentGatewayFieldsMayBeNull() {
        OrderRequestDTO dto = new OrderRequestDTO(
                5L,
                "COD",
                null,
                null,
                null,
                null
        );

        Set<ConstraintViolation<OrderRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void equalObjectsShouldBeEqualAndHaveSameHashCode() {
        OrderRequestDTO first = new OrderRequestDTO(
                7L, "CARD", "id-7", "SUCCESS", "Approved", "Stripe"
        );
        OrderRequestDTO second = new OrderRequestDTO(
                7L, "CARD", "id-7", "SUCCESS", "Approved", "Stripe"
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setPgStatus("FAILED");

        assertNotEquals(first, second);
    }

    @Test
    void toStringShouldContainFieldValues() {
        OrderRequestDTO dto = new OrderRequestDTO(
                3L, "UPI", "upi-123", "SUCCESS", "Completed", "Gateway"
        );

        String representation = dto.toString();

        assertTrue(representation.contains("addressId=3"));
        assertTrue(representation.contains("paymentType=UPI"));
        assertTrue(representation.contains("pgPaymentId=upi-123"));
        assertTrue(representation.contains("pgStatus=SUCCESS"));
        assertTrue(representation.contains("pgResponseMessage=Completed"));
        assertTrue(representation.contains("pgName=Gateway"));
    }

    private boolean hasViolation(
            OrderRequestDTO dto,
            String property,
            String message
    ) {
        return validator.validate(dto).stream()
                .anyMatch(violation ->
                        violation.getPropertyPath().toString().equals(property)
                                && violation.getMessage().equals(message));
    }
}
