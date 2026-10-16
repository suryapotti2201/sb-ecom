package com.ecommerce.project.payload;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemDTOTest {

    @Test
    void noArgsConstructorShouldInitializeFieldsToNull() {
        OrderItemDTO orderItem = new OrderItemDTO();

        assertNull(orderItem.getOrderItemId());
        assertNull(orderItem.getProduct());
        assertNull(orderItem.getQuantity());
        assertNull(orderItem.getDiscount());
        assertNull(orderItem.getOrderedProductPrice());
    }

    @Test
    void allArgsConstructorShouldInitializeAllFields() {
        ProductDTO product = new ProductDTO();
        BigDecimal discount = new BigDecimal("10.00");
        BigDecimal orderedProductPrice = new BigDecimal("99.99");

        OrderItemDTO orderItem = new OrderItemDTO(
                1L,
                product,
                2,
                discount,
                orderedProductPrice
        );

        assertEquals(1L, orderItem.getOrderItemId());
        assertSame(product, orderItem.getProduct());
        assertEquals(2, orderItem.getQuantity());
        assertEquals(discount, orderItem.getDiscount());
        assertEquals(orderedProductPrice, orderItem.getOrderedProductPrice());
    }

    @Test
    void settersAndGettersShouldUpdateAndReturnAllFields() {
        OrderItemDTO orderItem = new OrderItemDTO();
        ProductDTO product = new ProductDTO();
        BigDecimal discount = new BigDecimal("5.50");
        BigDecimal orderedProductPrice = new BigDecimal("45.00");

        orderItem.setOrderItemId(10L);
        orderItem.setProduct(product);
        orderItem.setQuantity(3);
        orderItem.setDiscount(discount);
        orderItem.setOrderedProductPrice(orderedProductPrice);

        assertEquals(10L, orderItem.getOrderItemId());
        assertSame(product, orderItem.getProduct());
        assertEquals(3, orderItem.getQuantity());
        assertEquals(discount, orderItem.getDiscount());
        assertEquals(orderedProductPrice, orderItem.getOrderedProductPrice());
    }

    @Test
    void settersShouldAllowFieldsToBeResetToNull() {
        OrderItemDTO orderItem = new OrderItemDTO(
                1L,
                new ProductDTO(),
                2,
                BigDecimal.ONE,
                BigDecimal.TEN
        );

        orderItem.setOrderItemId(null);
        orderItem.setProduct(null);
        orderItem.setQuantity(null);
        orderItem.setDiscount(null);
        orderItem.setOrderedProductPrice(null);

        assertNull(orderItem.getOrderItemId());
        assertNull(orderItem.getProduct());
        assertNull(orderItem.getQuantity());
        assertNull(orderItem.getDiscount());
        assertNull(orderItem.getOrderedProductPrice());
    }

    @Test
    void equalObjectsShouldBeEqualAndHaveSameHashCode() {
        ProductDTO product = new ProductDTO(
                "Laptop",
                "laptop.jpg",
                new BigDecimal("1000.00"),
                new BigDecimal("10.00"),
                new BigDecimal("900.00"),
                5L,
                'A'
        );

        OrderItemDTO first = new OrderItemDTO(
                1L,
                product,
                2,
                new BigDecimal("10.00"),
                new BigDecimal("900.00")
        );
        OrderItemDTO second = new OrderItemDTO(
                1L,
                product,
                2,
                new BigDecimal("10.00"),
                new BigDecimal("900.00")
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(first, first);
    }

    @Test
    void objectsWithDifferentFieldValuesShouldNotBeEqual() {
        OrderItemDTO first = new OrderItemDTO(
                1L,
                new ProductDTO(),
                2,
                BigDecimal.ONE,
                BigDecimal.TEN
        );
        OrderItemDTO differentQuantity = new OrderItemDTO(
                1L,
                first.getProduct(),
                3,
                BigDecimal.ONE,
                BigDecimal.TEN
        );

        assertNotEquals(first, differentQuantity);
        assertNotEquals(first, null);
        assertNotEquals(first, "not an order item");
    }

    @Test
    void toStringShouldContainFieldValues() {
        OrderItemDTO orderItem = new OrderItemDTO(
                7L,
                null,
                4,
                new BigDecimal("2.50"),
                new BigDecimal("25.00")
        );

        String representation = orderItem.toString();

        assertTrue(representation.contains("orderItemId=7"));
        assertTrue(representation.contains("product=null"));
        assertTrue(representation.contains("quantity=4"));
        assertTrue(representation.contains("discount=2.50"));
        assertTrue(representation.contains("orderedProductPrice=25.00"));
    }
}
