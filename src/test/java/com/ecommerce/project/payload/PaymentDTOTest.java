package com.ecommerce.project.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentDTOTest {

    @Test
    void noArgsConstructorCreatesDtoWithNullFields() {
        PaymentDTO payment = new PaymentDTO();

        assertNull(payment.getPaymentId());
        assertNull(payment.getPaymentType());
        assertNull(payment.getPgPaymentId());
        assertNull(payment.getPgStatus());
        assertNull(payment.getPgResponseMessage());
        assertNull(payment.getPgName());
    }

    @Test
    void allArgsConstructorInitializesAllFields() {
        PaymentDTO payment = new PaymentDTO(
                1L,
                "CARD",
                "pg-123",
                "SUCCESS",
                "Payment completed",
                "Stripe"
        );

        assertEquals(1L, payment.getPaymentId());
        assertEquals("CARD", payment.getPaymentType());
        assertEquals("pg-123", payment.getPgPaymentId());
        assertEquals("SUCCESS", payment.getPgStatus());
        assertEquals("Payment completed", payment.getPgResponseMessage());
        assertEquals("Stripe", payment.getPgName());
    }

    @Test
    void settersUpdateAllFields() {
        PaymentDTO payment = new PaymentDTO();

        payment.setPaymentId(2L);
        payment.setPaymentType("UPI");
        payment.setPgPaymentId("upi-456");
        payment.setPgStatus("PENDING");
        payment.setPgResponseMessage("Awaiting confirmation");
        payment.setPgName("PayU");

        assertEquals(2L, payment.getPaymentId());
        assertEquals("UPI", payment.getPaymentType());
        assertEquals("upi-456", payment.getPgPaymentId());
        assertEquals("PENDING", payment.getPgStatus());
        assertEquals("Awaiting confirmation", payment.getPgResponseMessage());
        assertEquals("PayU", payment.getPgName());
    }

    @Test
    void settersAllowFieldsToBeResetToNull() {
        PaymentDTO payment = new PaymentDTO(
                1L,
                "CARD",
                "pg-123",
                "SUCCESS",
                "Completed",
                "Stripe"
        );

        payment.setPaymentId(null);
        payment.setPaymentType(null);
        payment.setPgPaymentId(null);
        payment.setPgStatus(null);
        payment.setPgResponseMessage(null);
        payment.setPgName(null);

        assertNull(payment.getPaymentId());
        assertNull(payment.getPaymentType());
        assertNull(payment.getPgPaymentId());
        assertNull(payment.getPgStatus());
        assertNull(payment.getPgResponseMessage());
        assertNull(payment.getPgName());
    }

    @Test
    void equalDtosAreEqualAndHaveSameHashCode() {
        PaymentDTO first = new PaymentDTO(
                1L,
                "CARD",
                "pg-123",
                "SUCCESS",
                "Payment completed",
                "Stripe"
        );
        PaymentDTO second = new PaymentDTO(
                1L,
                "CARD",
                "pg-123",
                "SUCCESS",
                "Payment completed",
                "Stripe"
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void dtoIsNotEqualToDtoWithDifferentFieldValue() {
        PaymentDTO first = new PaymentDTO(
                1L,
                "CARD",
                "pg-123",
                "SUCCESS",
                "Payment completed",
                "Stripe"
        );
        PaymentDTO second = new PaymentDTO(
                1L,
                "CARD",
                "pg-123",
                "FAILED",
                "Payment completed",
                "Stripe"
        );

        assertNotEquals(first, second);
    }

    @Test
    void dtoIsNotEqualToNullOrDifferentType() {
        PaymentDTO payment = new PaymentDTO();

        assertNotEquals(null, payment);
        assertNotEquals(payment, "payment");
    }

    @Test
    void toStringContainsClassNameAndFieldValues() {
        PaymentDTO payment = new PaymentDTO(
                1L,
                "CARD",
                "pg-123",
                "SUCCESS",
                "Payment completed",
                "Stripe"
        );

        String representation = payment.toString();

        assertTrue(representation.contains("PaymentDTO"));
        assertTrue(representation.contains("paymentId=1"));
        assertTrue(representation.contains("paymentType=CARD"));
        assertTrue(representation.contains("pgPaymentId=pg-123"));
        assertTrue(representation.contains("pgStatus=SUCCESS"));
        assertTrue(representation.contains("pgResponseMessage=Payment completed"));
        assertTrue(representation.contains("pgName=Stripe"));
    }
}
