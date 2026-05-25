package com.ecommerce.project.service;

import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderRequestDTO;

public interface OrderService {
    OrderDTO placeOrder(OrderRequestDTO orderRequestDTO);

    OrderDTO placeOrder(OrderRequestDTO orderRequestDTO, Long productId);
}
