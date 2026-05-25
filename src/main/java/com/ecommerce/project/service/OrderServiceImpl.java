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
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        Address address = addressRepository.findById(orderRequestDTO.getAddressId())
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
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            return orderItem;
        }).collect(Collectors.toList());
        orderItems = orderItemRepository.saveAll(orderItems);

        List<Long> productIdsToDelete = new ArrayList<>();
        cart.getCartItems().forEach(cartItem -> {
            Product product = cartItem.getProduct();
            Integer quantity = cartItem.getQuantity();
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);
            productIdsToDelete.add(product.getProductId());
        });
        productIdsToDelete.forEach(productId ->
                        cartService.deleteProductFromCart(cart.getCartId(), productId));

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderDTO.setAddressId(address.getAddressId());
        List<OrderItemDTO> orderItemDTOS = orderItems.stream().map(orderItem ->
                modelMapper.map(orderItem, OrderItemDTO.class)).collect(Collectors.toList());
        orderDTO.setOrderItems(orderItemDTOS);
        return orderDTO;
    }

    @Override
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

        Address address = addressRepository.findById(orderRequestDTO.getAddressId())
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
        payment = paymentRepository.save(payment);
        order.setTotalAmount(cartItem.getProductPrice() * cartItem.getQuantity());
        order.setPayment(payment);
        Order savedOrder = orderRepository.save(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setOrderedProductPrice(cartItem.getProductPrice());
        orderItem.setDiscount(cartItem.getDiscount());
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem = orderItemRepository.save(orderItem);

        Product product = cartItem.getProduct();
        product.setQuantity(product.getQuantity() - cartItem.getQuantity());
        productRepository.save(product);
        cartService.deleteProductFromCart(cart.getCartId(), productId);

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderDTO.setAddressId(address.getAddressId());
        List<OrderItemDTO> orderItemDTOS = new ArrayList<>();
        OrderItemDTO orderItemDTO = modelMapper.map(orderItem, OrderItemDTO.class);
        orderItemDTOS.add(orderItemDTO);
        orderDTO.setOrderItems(orderItemDTOS);
        return orderDTO;
    }
}