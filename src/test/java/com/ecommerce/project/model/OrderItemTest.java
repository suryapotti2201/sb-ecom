package com.ecommerce.project.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void noArgsConstructorInitializesAllFieldsToNull() {
        OrderItem orderItem = new OrderItem();

        assertNull(orderItem.getOrderItemId());
        assertNull(orderItem.getOrder());
        assertNull(orderItem.getProduct());
        assertNull(orderItem.getQuantity());
        assertNull(orderItem.getDiscount());
        assertNull(orderItem.getOrderedProductPrice());
        assertNull(orderItem.getCreatedBy());
        assertNull(orderItem.getCreatedOn());
        assertNull(orderItem.getLastUpdatedBy());
        assertNull(orderItem.getLastUpdatedOn());
    }

    @Test
    void allArgsConstructorAndGettersPreserveProvidedValues() {
        Long orderItemId = 15L;
        Order order = new Order();
        Product product = new Product();
        Integer quantity = 3;
        BigDecimal discount = new BigDecimal("12.50");
        BigDecimal orderedProductPrice = new BigDecimal("87.50");
        String createdBy = "customer@example.com";
        LocalDateTime createdOn = LocalDateTime.of(2025, 1, 10, 14, 30);
        String lastUpdatedBy = "admin@example.com";
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 1, 11, 9, 15);

        OrderItem orderItem = new OrderItem(
                orderItemId,
                order,
                product,
                quantity,
                discount,
                orderedProductPrice,
                createdBy,
                createdOn,
                lastUpdatedBy,
                lastUpdatedOn
        );

        assertEquals(orderItemId, orderItem.getOrderItemId());
        assertSame(order, orderItem.getOrder());
        assertSame(product, orderItem.getProduct());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(discount, orderItem.getDiscount());
        assertEquals(orderedProductPrice, orderItem.getOrderedProductPrice());
        assertEquals(createdBy, orderItem.getCreatedBy());
        assertEquals(createdOn, orderItem.getCreatedOn());
        assertEquals(lastUpdatedBy, orderItem.getLastUpdatedBy());
        assertEquals(lastUpdatedOn, orderItem.getLastUpdatedOn());
    }

    @Test
    void settersUpdateAllFields() {
        OrderItem orderItem = new OrderItem();
        Order order = new Order();
        Product product = new Product();
        LocalDateTime createdOn = LocalDateTime.of(2025, 2, 1, 10, 0);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 2, 2, 11, 0);

        orderItem.setOrderItemId(21L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(5);
        orderItem.setDiscount(new BigDecimal("5.00"));
        orderItem.setOrderedProductPrice(new BigDecimal("100.00"));
        orderItem.setCreatedBy("user");
        orderItem.setCreatedOn(createdOn);
        orderItem.setLastUpdatedBy("operator");
        orderItem.setLastUpdatedOn(lastUpdatedOn);

        assertEquals(21L, orderItem.getOrderItemId());
        assertSame(order, orderItem.getOrder());
        assertSame(product, orderItem.getProduct());
        assertEquals(5, orderItem.getQuantity());
        assertEquals(new BigDecimal("5.00"), orderItem.getDiscount());
        assertEquals(new BigDecimal("100.00"), orderItem.getOrderedProductPrice());
        assertEquals("user", orderItem.getCreatedBy());
        assertEquals(createdOn, orderItem.getCreatedOn());
        assertEquals("operator", orderItem.getLastUpdatedBy());
        assertEquals(lastUpdatedOn, orderItem.getLastUpdatedOn());
    }

    @Test
    void settersAcceptNullValues() {
        OrderItem orderItem = new OrderItem(
                1L,
                new Order(),
                new Product(),
                2,
                BigDecimal.ONE,
                BigDecimal.TEN,
                "creator",
                LocalDateTime.of(2025, 1, 1, 1, 1),
                "updater",
                LocalDateTime.of(2025, 1, 2, 2, 2)
        );

        orderItem.setOrderItemId(null);
        orderItem.setOrder(null);
        orderItem.setProduct(null);
        orderItem.setQuantity(null);
        orderItem.setDiscount(null);
        orderItem.setOrderedProductPrice(null);
        orderItem.setCreatedBy(null);
        orderItem.setCreatedOn(null);
        orderItem.setLastUpdatedBy(null);
        orderItem.setLastUpdatedOn(null);

        assertNull(orderItem.getOrderItemId());
        assertNull(orderItem.getOrder());
        assertNull(orderItem.getProduct());
        assertNull(orderItem.getQuantity());
        assertNull(orderItem.getDiscount());
        assertNull(orderItem.getOrderedProductPrice());
        assertNull(orderItem.getCreatedBy());
        assertNull(orderItem.getCreatedOn());
        assertNull(orderItem.getLastUpdatedBy());
        assertNull(orderItem.getLastUpdatedOn());
    }

    @Test
    void equalOrderItemsAreEqualAndHaveSameHashCode() {
        Order order = new Order();
        Product product = new Product();
        LocalDateTime createdOn = LocalDateTime.of(2025, 3, 1, 8, 0);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 3, 2, 9, 0);

        OrderItem first = new OrderItem(
                7L,
                order,
                product,
                2,
                new BigDecimal("3.00"),
                new BigDecimal("47.00"),
                "creator",
                createdOn,
                "updater",
                lastUpdatedOn
        );
        OrderItem second = new OrderItem(
                7L,
                order,
                product,
                2,
                new BigDecimal("3.00"),
                new BigDecimal("47.00"),
                "creator",
                createdOn,
                "updater",
                lastUpdatedOn
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
