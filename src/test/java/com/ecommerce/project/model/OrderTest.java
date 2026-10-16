package com.ecommerce.project.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void noArgsConstructorInitializesAllFieldsToNull() {
        Order order = new Order();

        assertAll(
                () -> assertNull(order.getOrderId()),
                () -> assertNull(order.getEmailId()),
                () -> assertNull(order.getOrderItems()),
                () -> assertNull(order.getPayment()),
                () -> assertNull(order.getOrderDate()),
                () -> assertNull(order.getTotalAmount()),
                () -> assertNull(order.getStatus()),
                () -> assertNull(order.getAddress()),
                () -> assertNull(order.getCreatedBy()),
                () -> assertNull(order.getCreatedOn()),
                () -> assertNull(order.getLastUpdatedBy()),
                () -> assertNull(order.getLastUpdatedOn())
        );
    }

    @Test
    void allArgsConstructorPopulatesAllFields() {
        Long orderId = 10L;
        String emailId = "buyer@example.com";
        List<OrderItem> orderItems = List.of(new OrderItem());
        Payment payment = new Payment();
        LocalDate orderDate = LocalDate.of(2025, 1, 15);
        BigDecimal totalAmount = new BigDecimal("149.99");
        String status = "PLACED";
        Address address = new Address();
        String createdBy = "admin";
        LocalDateTime createdOn = LocalDateTime.of(2025, 1, 15, 10, 30);
        String lastUpdatedBy = "operator";
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 1, 16, 11, 45);

        Order order = new Order(
                orderId,
                emailId,
                orderItems,
                payment,
                orderDate,
                totalAmount,
                status,
                address,
                createdBy,
                createdOn,
                lastUpdatedBy,
                lastUpdatedOn
        );

        assertAll(
                () -> assertEquals(orderId, order.getOrderId()),
                () -> assertEquals(emailId, order.getEmailId()),
                () -> assertSame(orderItems, order.getOrderItems()),
                () -> assertSame(payment, order.getPayment()),
                () -> assertEquals(orderDate, order.getOrderDate()),
                () -> assertEquals(totalAmount, order.getTotalAmount()),
                () -> assertEquals(status, order.getStatus()),
                () -> assertSame(address, order.getAddress()),
                () -> assertEquals(createdBy, order.getCreatedBy()),
                () -> assertEquals(createdOn, order.getCreatedOn()),
                () -> assertEquals(lastUpdatedBy, order.getLastUpdatedBy()),
                () -> assertEquals(lastUpdatedOn, order.getLastUpdatedOn())
        );
    }

    @Test
    void settersUpdateAllFields() {
        Order order = new Order();

        Long orderId = 20L;
        String emailId = "customer@example.com";
        List<OrderItem> orderItems = new ArrayList<>();
        Payment payment = new Payment();
        LocalDate orderDate = LocalDate.of(2025, 2, 20);
        BigDecimal totalAmount = new BigDecimal("75.50");
        String status = "PACKED";
        Address address = new Address();
        String createdBy = "creator";
        LocalDateTime createdOn = LocalDateTime.of(2025, 2, 20, 9, 0);
        String lastUpdatedBy = "updater";
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 2, 20, 10, 0);

        order.setOrderId(orderId);
        order.setEmailId(emailId);
        order.setOrderItems(orderItems);
        order.setPayment(payment);
        order.setOrderDate(orderDate);
        order.setTotalAmount(totalAmount);
        order.setStatus(status);
        order.setAddress(address);
        order.setCreatedBy(createdBy);
        order.setCreatedOn(createdOn);
        order.setLastUpdatedBy(lastUpdatedBy);
        order.setLastUpdatedOn(lastUpdatedOn);

        assertAll(
                () -> assertEquals(orderId, order.getOrderId()),
                () -> assertEquals(emailId, order.getEmailId()),
                () -> assertSame(orderItems, order.getOrderItems()),
                () -> assertSame(payment, order.getPayment()),
                () -> assertEquals(orderDate, order.getOrderDate()),
                () -> assertEquals(totalAmount, order.getTotalAmount()),
                () -> assertEquals(status, order.getStatus()),
                () -> assertSame(address, order.getAddress()),
                () -> assertEquals(createdBy, order.getCreatedBy()),
                () -> assertEquals(createdOn, order.getCreatedOn()),
                () -> assertEquals(lastUpdatedBy, order.getLastUpdatedBy()),
                () -> assertEquals(lastUpdatedOn, order.getLastUpdatedOn())
        );
    }

    @Test
    void equalsAndHashCodeUseOrderState() {
        List<OrderItem> orderItems = new ArrayList<>();
        Payment payment = new Payment();
        Address address = new Address();

        Order first = new Order(
                1L,
                "buyer@example.com",
                orderItems,
                payment,
                LocalDate.of(2025, 3, 1),
                new BigDecimal("100.00"),
                "PLACED",
                address,
                "admin",
                LocalDateTime.of(2025, 3, 1, 8, 0),
                "admin",
                LocalDateTime.of(2025, 3, 1, 8, 0)
        );

        Order second = new Order(
                1L,
                "buyer@example.com",
                orderItems,
                payment,
                LocalDate.of(2025, 3, 1),
                new BigDecimal("100.00"),
                "PLACED",
                address,
                "admin",
                LocalDateTime.of(2025, 3, 1, 8, 0),
                "admin",
                LocalDateTime.of(2025, 3, 1, 8, 0)
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setStatus("DELIVERED");

        assertNotEquals(first, second);
    }

    @Test
    void toStringContainsOrderInformation() {
        Order order = new Order();
        order.setOrderId(5L);
        order.setEmailId("buyer@example.com");
        order.setStatus("PLACED");

        String representation = order.toString();

        assertNotNull(representation);
        assertTrue(representation.contains("Order"));
        assertTrue(representation.contains("buyer@example.com"));
        assertTrue(representation.contains("PLACED"));
    }
}
