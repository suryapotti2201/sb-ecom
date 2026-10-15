package com.ecommerce.project.service;

import com.ecommerce.project.model.Order;
import com.ecommerce.project.model.OrderItem;
import com.ecommerce.project.payload.SellerOrderDTO;
import com.ecommerce.project.response.SellerOrderResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface SellerOrderService {
    void createSellerOrdersForCheckout(Order order, List<OrderItem> orderItems);

    SellerOrderDTO updateOrderStatus(@Valid SellerOrderDTO sellerOrderDTO, Long sellerOrderId);

    SellerOrderResponse getSellerOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    SellerOrderResponse getSellerOrdersByStatus(String status, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}
