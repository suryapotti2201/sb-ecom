package com.ecommerce.project.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SellerOrderTest {

    @Test
    void noArgsConstructorInitializesAllFieldsToNull() {
        SellerOrder sellerOrder = new SellerOrder();

        assertAll(
                () -> assertNull(sellerOrder.getSellerOrderId()),
                () -> assertNull(sellerOrder.getOrder()),
                () -> assertNull(sellerOrder.getSeller()),
                () -> assertNull(sellerOrder.getOrderStatus()),
                () -> assertNull(sellerOrder.getRemark()),
                () -> assertNull(sellerOrder.getCreatedBy()),
                () -> assertNull(sellerOrder.getCreatedOn()),
                () -> assertNull(sellerOrder.getLastUpdatedBy()),
                () -> assertNull(sellerOrder.getLastUpdatedOn())
        );
    }

    @Test
    void allArgsConstructorAndGettersReturnProvidedValues() {
        Long sellerOrderId = 10L;
        Order order = new Order();
        User seller = new User("seller", "seller@example.com", "password");
        OrderStatus orderStatus = OrderStatus.PACKED;
        String remark = "Handle with care";
        String createdBy = "admin";
        LocalDateTime createdOn = LocalDateTime.of(2025, 1, 10, 12, 30);
        String lastUpdatedBy = "manager";
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 1, 11, 14, 45);

        SellerOrder sellerOrder = new SellerOrder(
                sellerOrderId,
                order,
                seller,
                orderStatus,
                remark,
                createdBy,
                createdOn,
                lastUpdatedBy,
                lastUpdatedOn
        );

        assertAll(
                () -> assertEquals(sellerOrderId, sellerOrder.getSellerOrderId()),
                () -> assertSame(order, sellerOrder.getOrder()),
                () -> assertSame(seller, sellerOrder.getSeller()),
                () -> assertEquals(orderStatus, sellerOrder.getOrderStatus()),
                () -> assertEquals(remark, sellerOrder.getRemark()),
                () -> assertEquals(createdBy, sellerOrder.getCreatedBy()),
                () -> assertEquals(createdOn, sellerOrder.getCreatedOn()),
                () -> assertEquals(lastUpdatedBy, sellerOrder.getLastUpdatedBy()),
                () -> assertEquals(lastUpdatedOn, sellerOrder.getLastUpdatedOn())
        );
    }

    @Test
    void settersUpdateAllFields() {
        SellerOrder sellerOrder = new SellerOrder();
        Long sellerOrderId = 20L;
        Order order = new Order();
        User seller = new User("seller", "seller@example.com", "password");
        LocalDateTime createdOn = LocalDateTime.of(2025, 2, 1, 9, 0);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 2, 2, 10, 0);

        sellerOrder.setSellerOrderId(sellerOrderId);
        sellerOrder.setOrder(order);
        sellerOrder.setSeller(seller);
        sellerOrder.setOrderStatus(OrderStatus.DELIVERED);
        sellerOrder.setRemark("Delivered successfully");
        sellerOrder.setCreatedBy("creator");
        sellerOrder.setCreatedOn(createdOn);
        sellerOrder.setLastUpdatedBy("updater");
        sellerOrder.setLastUpdatedOn(lastUpdatedOn);

        assertAll(
                () -> assertEquals(sellerOrderId, sellerOrder.getSellerOrderId()),
                () -> assertSame(order, sellerOrder.getOrder()),
                () -> assertSame(seller, sellerOrder.getSeller()),
                () -> assertEquals(OrderStatus.DELIVERED, sellerOrder.getOrderStatus()),
                () -> assertEquals("Delivered successfully", sellerOrder.getRemark()),
                () -> assertEquals("creator", sellerOrder.getCreatedBy()),
                () -> assertEquals(createdOn, sellerOrder.getCreatedOn()),
                () -> assertEquals("updater", sellerOrder.getLastUpdatedBy()),
                () -> assertEquals(lastUpdatedOn, sellerOrder.getLastUpdatedOn())
        );
    }

    @Test
    void equalInstancesHaveEqualHashCodes() {
        Order order = new Order();
        User seller = new User("seller", "seller@example.com", "password");
        LocalDateTime createdOn = LocalDateTime.of(2025, 3, 1, 8, 15);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 3, 2, 9, 30);

        SellerOrder first = new SellerOrder(
                1L,
                order,
                seller,
                OrderStatus.PLACED,
                "New order",
                "admin",
                createdOn,
                "admin",
                lastUpdatedOn
        );
        SellerOrder second = new SellerOrder(
                1L,
                order,
                seller,
                OrderStatus.PLACED,
                "New order",
                "admin",
                createdOn,
                "admin",
                lastUpdatedOn
        );

        assertEquals(first, second);
        assertEquals(second, first);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void instancesWithDifferentStateAreNotEqual() {
        SellerOrder first = new SellerOrder();
        SellerOrder second = new SellerOrder();

        first.setSellerOrderId(1L);
        second.setSellerOrderId(2L);

        assertNotEquals(first, second);
        assertNotEquals(first, null);
        assertNotEquals(first, "not a seller order");
    }

    @Test
    void toStringContainsClassNameAndFieldValues() {
        SellerOrder sellerOrder = new SellerOrder();
        sellerOrder.setSellerOrderId(5L);
        sellerOrder.setOrderStatus(OrderStatus.IN_TRANSIT);
        sellerOrder.setRemark("In transit");

        String value = sellerOrder.toString();

        assertAll(
                () -> assertTrue(value.contains("SellerOrder")),
                () -> assertTrue(value.contains("sellerOrderId=5")),
                () -> assertTrue(value.contains("orderStatus=IN_TRANSIT")),
                () -> assertTrue(value.contains("remark=In transit"))
        );
    }
}
