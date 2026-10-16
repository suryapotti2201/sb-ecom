package com.ecommerce.project.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void noArgsConstructorShouldInitializePaymentWithNullFields() {
        Payment payment = new Payment();

        assertAll(
                () -> assertNull(payment.getPaymentId()),
                () -> assertNull(payment.getOrder()),
                () -> assertNull(payment.getPaymentType()),
                () -> assertNull(payment.getPgPaymentId()),
                () -> assertNull(payment.getPgStatus()),
                () -> assertNull(payment.getPgResponseMessage()),
                () -> assertNull(payment.getPgName()),
                () -> assertNull(payment.getCreatedBy()),
                () -> assertNull(payment.getCreatedOn()),
                () -> assertNull(payment.getLastUpdatedBy()),
                () -> assertNull(payment.getLastUpdatedOn())
        );
    }

    @Test
    void paymentConstructorShouldSetPaymentGatewayFields() {
        Payment payment = new Payment(
                "CARD",
                "pg-payment-123",
                "SUCCESS",
                "Payment completed",
                "Stripe"
        );

        assertAll(
                () -> assertEquals("CARD", payment.getPaymentType()),
                () -> assertEquals("pg-payment-123", payment.getPgPaymentId()),
                () -> assertEquals("SUCCESS", payment.getPgStatus()),
                () -> assertEquals("Payment completed", payment.getPgResponseMessage()),
                () -> assertEquals("Stripe", payment.getPgName()),
                () -> assertNull(payment.getPaymentId()),
                () -> assertNull(payment.getOrder()),
                () -> assertNull(payment.getCreatedBy()),
                () -> assertNull(payment.getCreatedOn()),
                () -> assertNull(payment.getLastUpdatedBy()),
                () -> assertNull(payment.getLastUpdatedOn())
        );
    }

    @Test
    void allArgsConstructorShouldSetEveryField() {
        Order order = new Order();
        LocalDateTime createdOn = LocalDateTime.of(2025, 1, 10, 9, 30);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 1, 11, 14, 45);

        Payment payment = new Payment(
                42L,
                order,
                "PAYPAL",
                "paypal-payment-456",
                "COMPLETED",
                "Approved",
                "PayPal",
                "creator",
                createdOn,
                "updater",
                lastUpdatedOn
        );

        assertAll(
                () -> assertEquals(42L, payment.getPaymentId()),
                () -> assertSame(order, payment.getOrder()),
                () -> assertEquals("PAYPAL", payment.getPaymentType()),
                () -> assertEquals("paypal-payment-456", payment.getPgPaymentId()),
                () -> assertEquals("COMPLETED", payment.getPgStatus()),
                () -> assertEquals("Approved", payment.getPgResponseMessage()),
                () -> assertEquals("PayPal", payment.getPgName()),
                () -> assertEquals("creator", payment.getCreatedBy()),
                () -> assertEquals(createdOn, payment.getCreatedOn()),
                () -> assertEquals("updater", payment.getLastUpdatedBy()),
                () -> assertEquals(lastUpdatedOn, payment.getLastUpdatedOn())
        );
    }

    @Test
    void settersShouldUpdateEveryField() {
        Payment payment = new Payment();
        Order order = new Order();
        LocalDateTime createdOn = LocalDateTime.of(2025, 2, 1, 10, 15);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 2, 2, 16, 20);

        payment.setPaymentId(7L);
        payment.setOrder(order);
        payment.setPaymentType("UPI");
        payment.setPgPaymentId("upi-789");
        payment.setPgStatus("PENDING");
        payment.setPgResponseMessage("Awaiting confirmation");
        payment.setPgName("Razorpay");
        payment.setCreatedBy("admin");
        payment.setCreatedOn(createdOn);
        payment.setLastUpdatedBy("operator");
        payment.setLastUpdatedOn(lastUpdatedOn);

        assertAll(
                () -> assertEquals(7L, payment.getPaymentId()),
                () -> assertSame(order, payment.getOrder()),
                () -> assertEquals("UPI", payment.getPaymentType()),
                () -> assertEquals("upi-789", payment.getPgPaymentId()),
                () -> assertEquals("PENDING", payment.getPgStatus()),
                () -> assertEquals("Awaiting confirmation", payment.getPgResponseMessage()),
                () -> assertEquals("Razorpay", payment.getPgName()),
                () -> assertEquals("admin", payment.getCreatedBy()),
                () -> assertEquals(createdOn, payment.getCreatedOn()),
                () -> assertEquals("operator", payment.getLastUpdatedBy()),
                () -> assertEquals(lastUpdatedOn, payment.getLastUpdatedOn())
        );
    }

    @Test
    void equalPaymentsShouldBeEqualAndHaveSameHashCode() {
        LocalDateTime createdOn = LocalDateTime.of(2025, 3, 5, 11, 0);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 3, 6, 12, 0);

        Payment first = new Payment(
                10L,
                null,
                "CARD",
                "card-001",
                "SUCCESS",
                "Approved",
                "Gateway",
                "user",
                createdOn,
                "user",
                lastUpdatedOn
        );
        Payment second = new Payment(
                10L,
                null,
                "CARD",
                "card-001",
                "SUCCESS",
                "Approved",
                "Gateway",
                "user",
                createdOn,
                "user",
                lastUpdatedOn
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void paymentsWithDifferentValuesShouldNotBeEqual() {
        Payment first = new Payment("CARD", "payment-1", "SUCCESS", "Approved", "Gateway");
        Payment second = new Payment("CARD", "payment-2", "SUCCESS", "Approved", "Gateway");

        assertNotEquals(first, second);
        assertNotEquals(first, null);
        assertNotEquals(first, new Object());
    }

    @Test
    void toStringShouldContainPaymentInformation() {
        Payment payment = new Payment(
                "CARD",
                "payment-123",
                "SUCCESS",
                "Approved",
                "Stripe"
        );

        String representation = payment.toString();

        assertAll(
                () -> assertNotNull(representation),
                () -> assertTrue(representation.contains("CARD")),
                () -> assertTrue(representation.contains("payment-123")),
                () -> assertTrue(representation.contains("SUCCESS")),
                () -> assertTrue(representation.contains("Approved")),
                () -> assertTrue(representation.contains("Stripe"))
        );
    }
}
