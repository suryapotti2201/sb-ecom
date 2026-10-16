package com.ecommerce.project.controller;

import com.ecommerce.project.payload.SellerOrderDTO;
import com.ecommerce.project.response.SellerOrderResponse;
import com.ecommerce.project.service.SellerOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerOrderControllerTest {

    @Mock
    private SellerOrderService sellerOrderService;

    @InjectMocks
    private SellerOrderController sellerOrderController;

    @Test
    void updateOderStatusReturnsUpdatedOrder() {
        Long sellerOrderId = 15L;
        SellerOrderDTO sellerOrderDTO = org.mockito.Mockito.mock(SellerOrderDTO.class);
        SellerOrderDTO updatedSellerOrderDTO = org.mockito.Mockito.mock(SellerOrderDTO.class);

        when(sellerOrderService.updateOrderStatus(sellerOrderDTO, sellerOrderId))
                .thenReturn(updatedSellerOrderDTO);

        ResponseEntity<SellerOrderDTO> response =
                sellerOrderController.updateOderStatus(sellerOrderId, sellerOrderDTO);

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(updatedSellerOrderDTO, response.getBody());
        verify(sellerOrderService).updateOrderStatus(sellerOrderDTO, sellerOrderId);
    }

    @Test
    void updateOderStatusPropagatesServiceFailure() {
        Long sellerOrderId = 15L;
        SellerOrderDTO sellerOrderDTO = org.mockito.Mockito.mock(SellerOrderDTO.class);
        RuntimeException failure = new RuntimeException("unable to update order");

        when(sellerOrderService.updateOrderStatus(sellerOrderDTO, sellerOrderId))
                .thenThrow(failure);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> sellerOrderController.updateOderStatus(sellerOrderId, sellerOrderDTO)
        );

        assertSame(failure, thrown);
        verify(sellerOrderService).updateOrderStatus(sellerOrderDTO, sellerOrderId);
    }

    @Test
    void getSellerOrdersReturnsServiceResponse() throws Exception {
        Integer pageNumber = 2;
        Integer pageSize = 10;
        String sortBy = "orderDate";
        String sortOrder = "asc";
        SellerOrderResponse sellerOrderResponse =
                org.mockito.Mockito.mock(SellerOrderResponse.class);

        when(sellerOrderService.getSellerOrders(pageNumber, pageSize, sortBy, sortOrder))
                .thenReturn(sellerOrderResponse);

        ResponseEntity<SellerOrderResponse> response = invokePrivateMethod(
                "getSellerOrders",
                new Class<?>[]{Integer.class, Integer.class, String.class, String.class},
                pageNumber, pageSize, sortBy, sortOrder
        );

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(sellerOrderResponse, response.getBody());
        verify(sellerOrderService).getSellerOrders(pageNumber, pageSize, sortBy, sortOrder);
    }

    @Test
    void getSellerOrdersByStatusReturnsServiceResponse() throws Exception {
        String status = "PACKED";
        Integer pageNumber = 1;
        Integer pageSize = 20;
        String sortBy = "id";
        String sortOrder = "desc";
        SellerOrderResponse sellerOrderResponse =
                org.mockito.Mockito.mock(SellerOrderResponse.class);

        when(sellerOrderService.getSellerOrdersByStatus(
                status, pageNumber, pageSize, sortBy, sortOrder))
                .thenReturn(sellerOrderResponse);

        ResponseEntity<SellerOrderResponse> response = invokePrivateMethod(
                "getSellerOrdersByStatus",
                new Class<?>[]{
                        String.class, Integer.class, Integer.class, String.class, String.class
                },
                status, pageNumber, pageSize, sortBy, sortOrder
        );

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertSame(sellerOrderResponse, response.getBody());
        verify(sellerOrderService).getSellerOrdersByStatus(
                status, pageNumber, pageSize, sortBy, sortOrder);
    }

    @Test
    void getSellerOrdersByStatusPropagatesServiceFailure() throws Exception {
        String status = "DELIVERED";
        Integer pageNumber = 1;
        Integer pageSize = 10;
        String sortBy = "id";
        String sortOrder = "asc";

        RuntimeException failure =
                new RuntimeException("unable to retrieve orders");

        when(sellerOrderService.getSellerOrdersByStatus(
                status, pageNumber, pageSize, sortBy, sortOrder))
                .thenThrow(failure);

        InvocationTargetException thrown = assertThrows(
                InvocationTargetException.class,
                () -> invokePrivateMethod(
                        "getSellerOrdersByStatus",
                        new Class<?>[]{
                                String.class,
                                Integer.class,
                                Integer.class,
                                String.class,
                                String.class
                        },
                        status, pageNumber, pageSize, sortBy, sortOrder
                )
        );

        assertSame(failure, thrown.getCause());

        verify(sellerOrderService).getSellerOrdersByStatus(
                status, pageNumber, pageSize, sortBy, sortOrder);
    }

    @SuppressWarnings("unchecked")
    private <T> T invokePrivateMethod(
            String methodName,
            Class<?>[] parameterTypes,
            Object... arguments) throws Exception {

        Method method = SellerOrderController.class.getDeclaredMethod(
                methodName, parameterTypes);
        method.setAccessible(true);
        return (T) method.invoke(sellerOrderController, arguments);
    }
}
