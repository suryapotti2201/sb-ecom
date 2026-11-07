package com.ecommerce.project.controller;

import lombok.extern.slf4j.Slf4j;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderRequestDTO;
import com.ecommerce.project.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> placeOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO){
        log.info("OrderController => placeOrder : In");
        OrderDTO orderDTO = orderService.placeOrder(orderRequestDTO);
        log.info("OrderController => placeOrder : Out");
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<OrderDTO> placeOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO,
            @PathVariable @Positive(message = "Product ID must be greater than zero") Long productId){
        log.info("OrderController => placeOrder : In");
        OrderDTO orderDTO = orderService.placeOrder(orderRequestDTO, productId);
        log.info("OrderController => placeOrder : Out");
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }
}
