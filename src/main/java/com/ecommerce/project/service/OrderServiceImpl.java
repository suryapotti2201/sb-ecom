package com.ecommerce.project.service;

import com.ecommerce.project.constants.SystemConstants;
import com.ecommerce.project.exception.EcommerceException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderItemDTO;
import com.ecommerce.project.payload.OrderRequestDTO;
import com.ecommerce.project.repository.*;
import com.ecommerce.project.util.AuthUtil;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(OrderRequestDTO orderRequestDTO) {
        String emailId = authUtil.loggedInEmailId();
        Cart cart = cartRepository.findByEmailId(emailId);
        if(cart == null){
            throw new ResourceNotFoundException("Cart", "emailId", emailId);
        }

        Address address = addressRepository.findByIdAndEmailId(orderRequestDTO.getAddressId(), authUtil.loggedInEmailId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Address", "addressId",
                                orderRequestDTO.getAddressId()));

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setAddress(address);
        order.setEmailId(emailId);
        order.setStatus("Order Accepted");
        order.setTotalAmount(cart.getTotalPrice());

        Payment payment = new Payment(orderRequestDTO.getPaymentType(),
                orderRequestDTO.getPgPaymentId(), orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage(), orderRequestDTO.getPgName());

        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems.isEmpty()){
            throw new EcommerceException("Cart is Empty!");
        }

        List<CartItem> itemsByProductId = cartItems.stream()
                .sorted(java.util.Comparator.comparing(
                        item -> item.getProduct().getProductId()))
                .toList();

        for (CartItem cartItem : itemsByProductId) {
            decreaseStock(
                    cartItem.getProduct().getProductId(),
                    cartItem.getQuantity()
            );
        }

        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);
        order.setCreatedBy(authUtil.loggedInUser().getUserName());
        order.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        order.setCreatedOn(LocalDateTime.now());
        order.setLastUpdatedOn(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setCreatedBy(authUtil.loggedInUser().getUserName());
            orderItem.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
            orderItem.setCreatedOn(LocalDateTime.now());
            orderItem.setLastUpdatedOn(LocalDateTime.now());
            return orderItem;
        }).collect(Collectors.toList());
        orderItems = orderItemRepository.saveAll(orderItems);

        List<Long> productIdsToDelete = cartItems.stream().map(item -> item.getProduct().getProductId())
                .toList();
        productIdsToDelete.forEach(productId -> cartService.deleteProductFromCart(cart.getCartId(), productId));

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderDTO.setAddressId(address.getAddressId());
        List<OrderItemDTO> orderItemDTOS = orderItems.stream().map(orderItem ->
                modelMapper.map(orderItem, OrderItemDTO.class)).collect(Collectors.toList());
        orderDTO.setOrderItems(orderItemDTOS);
        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO placeOrder(OrderRequestDTO orderRequestDTO, Long productId) {
        String emailId = authUtil.loggedInEmailId();

        productRepository.findByProductIdAndStatus(productId, SystemConstants.ACTIVE)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", "productId", productId)
                );

        Cart cart = cartRepository.findByEmailId(emailId);
        if(cart == null){
            throw new ResourceNotFoundException("Cart", "emailId", emailId);
        }

        Address address = addressRepository.findByIdAndEmailId(orderRequestDTO.getAddressId(), authUtil.loggedInEmailId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Address", "addressId",
                                orderRequestDTO.getAddressId()));

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setAddress(address);
        order.setEmailId(emailId);
        order.setStatus("Order Accepted");

        Payment payment = new Payment(orderRequestDTO.getPaymentType(),
                orderRequestDTO.getPgPaymentId(), orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage(), orderRequestDTO.getPgName());
        payment.setOrder(order);

        CartItem cartItem = cartItemRepository.findByProductIdAndCardId(productId, cart.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId));
        decreaseStock(productId, cartItem.getQuantity());
        payment = paymentRepository.save(payment);
        order.setTotalAmount(cartItem.getProductPrice().multiply(
                BigDecimal.valueOf(cartItem.getQuantity())));
        order.setPayment(payment);
        order.setCreatedBy(authUtil.loggedInUser().getUserName());
        order.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        order.setCreatedOn(LocalDateTime.now());
        order.setLastUpdatedOn(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setOrderedProductPrice(cartItem.getProductPrice());
        orderItem.setDiscount(cartItem.getDiscount());
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setCreatedBy(authUtil.loggedInUser().getUserName());
        orderItem.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        orderItem.setCreatedOn(LocalDateTime.now());
        orderItem.setLastUpdatedOn(LocalDateTime.now());
        orderItem = orderItemRepository.save(orderItem);
        cartService.deleteProductFromCart(cart.getCartId(), productId);

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderDTO.setAddressId(address.getAddressId());
        List<OrderItemDTO> orderItemDTOS = new ArrayList<>();
        OrderItemDTO orderItemDTO = modelMapper.map(orderItem, OrderItemDTO.class);
        orderItemDTOS.add(orderItemDTO);
        orderDTO.setOrderItems(orderItemDTOS);
        return orderDTO;
    }

    private void decreaseStock(Long productId, Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new EcommerceException("Quantity must be at least 1");
        }

        int updatedRows = productRepository.decreaseStockIfAvailable(productId, quantity.longValue(),
                SystemConstants.ACTIVE, SystemConstants.SYSTEM, LocalDateTime.now());
        if (updatedRows != 1) {
            throw new EcommerceException("Product " + productId + " is unavailable or has insufficient stock"
            );
        }
    }
}