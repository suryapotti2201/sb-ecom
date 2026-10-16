package com.ecommerce.project.controller;

import com.ecommerce.project.constants.SystemConstants;
import com.ecommerce.project.payload.SellerOrderDTO;
import com.ecommerce.project.response.SellerOrderResponse;
import com.ecommerce.project.service.SellerOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app")
public class SellerOrderController {

    @Autowired
    private SellerOrderService sellerOrderService;

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PatchMapping("/seller/orders/{sellerOrderId}/status")
    public ResponseEntity<SellerOrderDTO> updateOderStatus(
            @PathVariable @Positive(message = "Seller Order ID must be greater than zero") Long sellerOrderId,
            @Valid @RequestBody SellerOrderDTO sellerOrderDTO){
        SellerOrderDTO updatedSellerOrderDTO = sellerOrderService.updateOrderStatus(sellerOrderDTO, sellerOrderId);
        return ResponseEntity.ok(updatedSellerOrderDTO);
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @GetMapping("/seller/orders")
    private ResponseEntity<SellerOrderResponse> getSellerOrders(
            @RequestParam(value = "pageNumber", defaultValue = SystemConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = SystemConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = SystemConstants.SORT_SELLER_ORDER_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = SystemConstants.SORT_DIR) String sortOrder) {
        SellerOrderResponse sellerOrderResponse =  sellerOrderService.getSellerOrders(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(sellerOrderResponse);
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @GetMapping("/seller/orders/{status}")
    private ResponseEntity<SellerOrderResponse> getSellerOrdersByStatus(
            @PathVariable @Pattern(regexp = "^(PLACED|PACKED|IN_TRANSIT|OUT_FOR_DELIVERY|DELIVERED)$",
                    message = "Order status must be PLACED or PACKED, IN_TRANSIT, OUT_FOR_DELIVERY, or DELIVERED") String status,
            @RequestParam(value = "pageNumber", defaultValue = SystemConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = SystemConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = SystemConstants.SORT_SELLER_ORDER_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = SystemConstants.SORT_DIR) String sortOrder) {
        SellerOrderResponse sellerOrderResponse =  sellerOrderService.getSellerOrdersByStatus(status, pageNumber,
                pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(sellerOrderResponse);
    }
}
