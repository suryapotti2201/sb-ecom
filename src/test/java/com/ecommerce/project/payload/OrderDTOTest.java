package com.ecommerce.project.payload;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderDTOTest {

    @Test
    void noArgsConstructorInitializesFieldsToNull() {
        OrderDTO order = new OrderDTO();

        assertNull(order.getOrderId());
        assertNull(order.getEmailId());
        assertNull(order.getOrderItems());
        assertNull(order.getOrderDate());
        assertNull(order.getPayment());
        assertNull(order.getTotalAmount());
        assertNull(order.getOrderStatus());
        assertNull(order.getAddressId());
    }

    @Test
    void allArgsConstructorStoresAllValues() {
        Long orderId = 101L;
        String emailId = "customer@example.com";
        OrderItemDTO item = new OrderItemDTO();
        List<OrderItemDTO> orderItems = List.of(item);
        LocalDate orderDate = LocalDate.of(2025, 1, 15);
        PaymentDTO payment = new PaymentDTO();
        BigDecimal totalAmount = new BigDecimal("149.99");
        String orderStatus = "DELIVERED";
        Long addressId = 55L;

        OrderDTO order = new OrderDTO(
                orderId,
                emailId,
                orderItems,
                orderDate,
                payment,
                totalAmount,
                orderStatus,
                addressId
        );

        assertEquals(orderId, order.getOrderId());
        assertEquals(emailId, order.getEmailId());
        assertSame(orderItems, order.getOrderItems());
        assertEquals(orderDate, order.getOrderDate());
        assertSame(payment, order.getPayment());
        assertEquals(totalAmount, order.getTotalAmount());
        assertEquals(orderStatus, order.getOrderStatus());
        assertEquals(addressId, order.getAddressId());
    }

    @Test
    void settersUpdateAllFieldsAndGettersReturnUpdatedValues() {
        OrderDTO order = new OrderDTO();
        Long orderId = 202L;
        String emailId = "updated@example.com";
        List<OrderItemDTO> orderItems = List.of(new OrderItemDTO());
        LocalDate orderDate = LocalDate.of(2025, 2, 20);
        PaymentDTO payment = new PaymentDTO();
        BigDecimal totalAmount = new BigDecimal("275.50");
        String orderStatus = "PACKED";
        Long addressId = 88L;

        order.setOrderId(orderId);
        order.setEmailId(emailId);
        order.setOrderItems(orderItems);
        order.setOrderDate(orderDate);
        order.setPayment(payment);
        order.setTotalAmount(totalAmount);
        order.setOrderStatus(orderStatus);
        order.setAddressId(addressId);

        assertEquals(orderId, order.getOrderId());
        assertEquals(emailId, order.getEmailId());
        assertSame(orderItems, order.getOrderItems());
        assertEquals(orderDate, order.getOrderDate());
        assertSame(payment, order.getPayment());
        assertEquals(totalAmount, order.getTotalAmount());
        assertEquals(orderStatus, order.getOrderStatus());
        assertEquals(addressId, order.getAddressId());
    }

    @Test
    void settersAllowFieldsToBeResetToNull() {
        OrderDTO order = new OrderDTO(
                1L,
                "customer@example.com",
                List.of(new OrderItemDTO()),
                LocalDate.now(),
                new PaymentDTO(),
                BigDecimal.TEN,
                "PLACED",
                2L
        );

        order.setOrderId(null);
        order.setEmailId(null);
        order.setOrderItems(null);
        order.setOrderDate(null);
        order.setPayment(null);
        order.setTotalAmount(null);
        order.setOrderStatus(null);
        order.setAddressId(null);

        assertNull(order.getOrderId());
        assertNull(order.getEmailId());
        assertNull(order.getOrderItems());
        assertNull(order.getOrderDate());
        assertNull(order.getPayment());
        assertNull(order.getTotalAmount());
        assertNull(order.getOrderStatus());
        assertNull(order.getAddressId());
    }

    @Test
    void equalsAndHashCodeAreValueBased() {
        OrderDTO first = createOrder();
        OrderDTO second = createOrder();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(first, first);
    }

    @Test
    void equalsReturnsFalseForDifferentValuesAndNull() {
        OrderDTO order = createOrder();
        OrderDTO differentOrder = createOrder();
        differentOrder.setOrderStatus("CANCELLED");

        assertNotEquals(order, differentOrder);
        assertNotEquals(order, null);
        assertNotEquals(order, "not an order");
    }

    @Test
    void toStringContainsFieldValues() {
        OrderDTO order = createOrder();

        String representation = order.toString();

        assertTrue(representation.contains("orderId=101"));
        assertTrue(representation.contains("emailId=customer@example.com"));
        assertTrue(representation.contains("orderDate=2025-01-15"));
        assertTrue(representation.contains("totalAmount=149.99"));
        assertTrue(representation.contains("orderStatus=DELIVERED"));
        assertTrue(representation.contains("addressId=55"));
    }

    private OrderDTO createOrder() {
        return new OrderDTO(
                101L,
                "customer@example.com",
                List.of(new OrderItemDTO()),
                LocalDate.of(2025, 1, 15),
                new PaymentDTO(),
                new BigDecimal("149.99"),
                "DELIVERED",
                55L
        );
    }
}
