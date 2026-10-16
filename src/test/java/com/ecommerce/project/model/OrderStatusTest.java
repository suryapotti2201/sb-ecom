package com.ecommerce.project.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderStatusTest {

    @Test
    void values_returnsStatusesInWorkflowOrder() {
        assertArrayEquals(
                new OrderStatus[]{
                        OrderStatus.PLACED,
                        OrderStatus.PACKED,
                        OrderStatus.IN_TRANSIT,
                        OrderStatus.OUT_FOR_DELIVERY,
                        OrderStatus.DELIVERED
                },
                OrderStatus.values()
        );
    }

    @Test
    void valueOf_returnsMatchingStatusForEachValidName() {
        for (OrderStatus status : OrderStatus.values()) {
            assertEquals(status, OrderStatus.valueOf(status.name()));
        }
    }

    @Test
    void valueOf_throwsExceptionForUnknownName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> OrderStatus.valueOf("UNKNOWN")
        );
    }

    @Test
    void valueOf_throwsExceptionForNullName() {
        assertThrows(
                NullPointerException.class,
                () -> OrderStatus.valueOf(null)
        );
    }

    @Test
    void statusNames_matchExpectedSerializedNames() {
        assertEquals("PLACED", OrderStatus.PLACED.name());
        assertEquals("PACKED", OrderStatus.PACKED.name());
        assertEquals("IN_TRANSIT", OrderStatus.IN_TRANSIT.name());
        assertEquals("OUT_FOR_DELIVERY", OrderStatus.OUT_FOR_DELIVERY.name());
        assertEquals("DELIVERED", OrderStatus.DELIVERED.name());
    }
}
