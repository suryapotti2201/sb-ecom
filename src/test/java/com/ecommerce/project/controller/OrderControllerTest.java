package com.ecommerce.project.controller;

import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderRequestDTO;
import com.ecommerce.project.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    void placeOrderWithoutProductIdReturnsCreatedOrder() {
        OrderRequestDTO request = new OrderRequestDTO();
        OrderDTO expectedOrder = org.mockito.Mockito.mock(OrderDTO.class);

        when(orderService.placeOrder(request)).thenReturn(expectedOrder);

        ResponseEntity<OrderDTO> response = orderController.placeOrder(request);

        assertSame(HttpStatus.CREATED, response.getStatusCode());
        assertSame(expectedOrder, response.getBody());
        verify(orderService).placeOrder(request);
    }

    @Test
    void placeOrderWithProductIdReturnsCreatedOrder() {
        OrderRequestDTO request = new OrderRequestDTO();
        OrderDTO expectedOrder = org.mockito.Mockito.mock(OrderDTO.class);
        Long productId = 42L;

        when(orderService.placeOrder(request, productId)).thenReturn(expectedOrder);

        ResponseEntity<OrderDTO> response = orderController.placeOrder(request, productId);

        assertSame(HttpStatus.CREATED, response.getStatusCode());
        assertSame(expectedOrder, response.getBody());
        verify(orderService).placeOrder(request, productId);
    }

    @Test
    void placeOrderWithoutProductIdPropagatesServiceException() {
        OrderRequestDTO request = new OrderRequestDTO();
        RuntimeException expectedException = new RuntimeException("Unable to place order");

        when(orderService.placeOrder(request)).thenThrow(expectedException);

        RuntimeException actualException = assertThrows(
                RuntimeException.class,
                () -> orderController.placeOrder(request)
        );

        assertSame(expectedException, actualException);
        verify(orderService).placeOrder(request);
    }

    @Test
    void placeOrderWithProductIdPropagatesServiceException() {
        OrderRequestDTO request = new OrderRequestDTO();
        Long productId = 42L;
        RuntimeException expectedException = new RuntimeException("Unable to place order");

        when(orderService.placeOrder(request, productId)).thenThrow(expectedException);

        RuntimeException actualException = assertThrows(
                RuntimeException.class,
                () -> orderController.placeOrder(request, productId)
        );

        assertSame(expectedException, actualException);
        verify(orderService).placeOrder(request, productId);
    }
}
