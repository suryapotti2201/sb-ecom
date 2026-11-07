package com.ecommerce.project.service;


import lombok.extern.slf4j.Slf4j;
import com.ecommerce.project.constants.SystemConstants;
import com.ecommerce.project.exception.EcommerceException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.payload.SellerOrderDTO;
import com.ecommerce.project.repository.SellerOrderRepository;
import com.ecommerce.project.response.SellerOrderResponse;
import com.ecommerce.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class SellerOrderServiceImpl implements SellerOrderService {

    @Autowired
    private SellerOrderRepository sellerOrderRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void createSellerOrdersForCheckout(Order order, List<OrderItem> orderItems) {
        log.info("SellerOrderServiceImpl => createSellerOrdersForCheckout : In");
        List<SellerOrder> sellerOrders = new ArrayList<>();
        Stream<User> sellers = orderItems.stream().map(orderItem ->
                orderItem.getProduct().getSeller()).distinct();
        sellers.forEach(seller -> {
            SellerOrder sellerOrder = new SellerOrder();
            sellerOrder.setOrder(order);
            sellerOrder.setSeller(seller);
            sellerOrder.setOrderStatus(OrderStatus.PLACED);
            sellerOrder.setCreatedBy(SystemConstants.SYSTEM);
            sellerOrder.setLastUpdatedBy(SystemConstants.SYSTEM);
            sellerOrder.setCreatedOn(LocalDateTime.now());
            sellerOrder.setLastUpdatedOn(LocalDateTime.now());
            sellerOrders.add(sellerOrder);
        });
        sellerOrderRepository.saveAll(sellerOrders);
        log.info("SellerOrderServiceImpl => createSellerOrdersForCheckout : Out");
    }

    @Override
    public SellerOrderDTO updateOrderStatus(SellerOrderDTO sellerOrderDTO, Long sellerOrderId) {
        log.info("SellerOrderServiceImpl => updateOrderStatus : In");
        OrderStatus requestedStatus;
        try {
            requestedStatus = OrderStatus.valueOf(sellerOrderDTO.getOrderStatus().trim());
        } catch (IllegalArgumentException exception) {
            throw new EcommerceException("Unsupported order status");
        }

        SellerOrder sellerOrder = sellerOrderRepository.findBySellerOrderIdAndSellerId(sellerOrderId,
                authUtil.loggedInUserId()).orElseThrow(
                        () -> new ResourceNotFoundException("SellerOrder", "sellerOrderId", sellerOrderId));

        if (sellerOrder.getOrderStatus() == requestedStatus) {
            log.info("SellerOrderServiceImpl => updateOrderStatus : Out");
            return modelMapper.map(sellerOrder, SellerOrderDTO.class);
        }

        if (!isValidStatusTransition(sellerOrder.getOrderStatus(), requestedStatus)) {
            throw new EcommerceException("Cannot change order status from " + sellerOrder.getOrderStatus() + " to "
                    + requestedStatus
            );
        }

        sellerOrder.setOrderStatus(requestedStatus);
        sellerOrder.setRemark(sellerOrderDTO.getRemark());
        sellerOrder.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        sellerOrder.setLastUpdatedOn(LocalDateTime.now());
        SellerOrder savedSellerOrder = sellerOrderRepository.save(sellerOrder);
        log.info("SellerOrderServiceImpl => updateOrderStatus : Out");
        return modelMapper.map(savedSellerOrder, SellerOrderDTO.class);
    }

    @Override
    public SellerOrderResponse getSellerOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        log.info("SellerOrderServiceImpl => getSellerOrders : In");
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase(SystemConstants.ASC)? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<SellerOrder> sellerOrderPage = sellerOrderRepository.findAllByUserId(authUtil.loggedInUserId(), pageDetails);
        List<SellerOrder> sellerOrders = sellerOrderPage.getContent();
        List<SellerOrderDTO> sellerOrderDTOs = sellerOrders.stream().map(sellerOrder ->
                modelMapper.map(sellerOrder, SellerOrderDTO.class)).toList();
        SellerOrderResponse sellerOrderResponse = new SellerOrderResponse();
        sellerOrderResponse.setContent(sellerOrderDTOs);
        sellerOrderResponse.setPageNumber(sellerOrderPage.getNumber());
        sellerOrderResponse.setPageSize(sellerOrderPage.getSize());
        sellerOrderResponse.setTotalElements(sellerOrderPage.getTotalElements());
        sellerOrderResponse.setTotalPages(sellerOrderPage.getTotalPages());
        sellerOrderResponse.setLastPage(sellerOrderPage.isLast());
        log.info("SellerOrderServiceImpl => getSellerOrders : Out");
        return sellerOrderResponse;
    }

    @Override
    public SellerOrderResponse getSellerOrdersByStatus(String status, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        log.info("SellerOrderServiceImpl => getSellerOrdersByStatus : In");
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status);
        } catch (IllegalArgumentException exception) {
            throw new EcommerceException("Unsupported order status");
        }
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase(SystemConstants.ASC)? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<SellerOrder> sellerOrderPage = sellerOrderRepository.findAllByUserIdAndStatus(authUtil.loggedInUserId(),
                orderStatus, pageDetails);
        List<SellerOrder> sellerOrders = sellerOrderPage.getContent();
        List<SellerOrderDTO> sellerOrderDTOs = sellerOrders.stream().map(sellerOrder ->
                modelMapper.map(sellerOrder, SellerOrderDTO.class)).toList();
        SellerOrderResponse sellerOrderResponse = new SellerOrderResponse();
        sellerOrderResponse.setContent(sellerOrderDTOs);
        sellerOrderResponse.setPageNumber(sellerOrderPage.getNumber());
        sellerOrderResponse.setPageSize(sellerOrderPage.getSize());
        sellerOrderResponse.setTotalElements(sellerOrderPage.getTotalElements());
        sellerOrderResponse.setTotalPages(sellerOrderPage.getTotalPages());
        sellerOrderResponse.setLastPage(sellerOrderPage.isLast());
        log.info("SellerOrderServiceImpl => getSellerOrdersByStatus : Out");
        return sellerOrderResponse;
    }

    private boolean isValidStatusTransition(
            OrderStatus currentStatus, OrderStatus requestedStatus) {
        log.info("SellerOrderServiceImpl => isValidStatusTransition : In");

        if (currentStatus == null) {
            log.info("SellerOrderServiceImpl => isValidStatusTransition : Out");
            return false;
        }
        log.info("SellerOrderServiceImpl => isValidStatusTransition : Out");
        return switch (currentStatus) {
            case PLACED -> requestedStatus == OrderStatus.PACKED;
            case PACKED -> requestedStatus == OrderStatus.IN_TRANSIT;
            case IN_TRANSIT ->
                    requestedStatus == OrderStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY ->
                    requestedStatus == OrderStatus.DELIVERED;
            case DELIVERED -> false;
        };
    }
}
