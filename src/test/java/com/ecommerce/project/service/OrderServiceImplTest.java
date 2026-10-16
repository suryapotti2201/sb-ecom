package com.ecommerce.project.service;

import com.ecommerce.project.constants.SystemConstants;
import com.ecommerce.project.exception.EcommerceException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Order;
import com.ecommerce.project.model.OrderItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderItemDTO;
import com.ecommerce.project.payload.OrderRequestDTO;
import com.ecommerce.project.repository.AddressRepository;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.OrderItemRepository;
import com.ecommerce.project.repository.OrderRepository;
import com.ecommerce.project.repository.PaymentRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    private static final String CUSTOMER_EMAIL = "customer@example.com";
    private static final Long ADDRESS_ID = 10L;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private SellerOrderService sellerOrderService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequestDTO orderRequest;
    private Address address;
    private User user;

    @BeforeEach
    void setUp() {
        orderRequest = new OrderRequestDTO();
        orderRequest.setAddressId(ADDRESS_ID);

        address = new Address();
        address.setAddressId(ADDRESS_ID);

        user = new User();
        user.setUserName("customer");
    }

    @Test
    void placeOrderWithCartOrdersAllItemsAndRemovesThemFromCart() {
        Product firstProduct = product(1L);
        Product secondProduct = product(2L);

        CartItem firstItem =
                cartItem(firstProduct, new BigDecimal("12.50"), 2);
        CartItem secondItem =
                cartItem(secondProduct, new BigDecimal("5.00"), 3);

        Cart cart = cart(55L, secondItem, firstItem);

        Order savedOrder = new Order();
        OrderDTO orderDTO = new OrderDTO();
        OrderItemDTO firstOrderItemDTO = new OrderItemDTO();
        OrderItemDTO secondOrderItemDTO = new OrderItemDTO();

        stubCustomerEmail();
        stubAddressLookup();
        stubAuditUser();

        when(cartRepository.findByEmailId(CUSTOMER_EMAIL))
                .thenReturn(cart);

        when(productRepository.decreaseStockIfAvailable(
                eq(1L),
                eq(2L),
                eq(SystemConstants.ACTIVE),
                eq(SystemConstants.SYSTEM),
                any()))
                .thenReturn(1);

        when(productRepository.decreaseStockIfAvailable(
                eq(2L),
                eq(3L),
                eq(SystemConstants.ACTIVE),
                eq(SystemConstants.SYSTEM),
                any()))
                .thenReturn(1);

        when(paymentRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        when(orderItemRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(modelMapper.map(savedOrder, OrderDTO.class))
                .thenReturn(orderDTO);

        when(modelMapper.map(
                any(OrderItem.class), eq(OrderItemDTO.class)))
                .thenReturn(firstOrderItemDTO, secondOrderItemDTO);

        OrderDTO result = orderService.placeOrder(orderRequest);

        assertSame(orderDTO, result);
        assertEquals(new BigDecimal("40.00"), cart.getTotalPrice());

        ArgumentCaptor<Order> orderCaptor =
                ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).save(orderCaptor.capture());

        Order createdOrder = orderCaptor.getValue();

        assertEquals(
                new BigDecimal("40.00"),
                createdOrder.getTotalAmount()
        );
        assertEquals(CUSTOMER_EMAIL, createdOrder.getEmailId());
        assertEquals("Order Accepted", createdOrder.getStatus());

        verify(productRepository).decreaseStockIfAvailable(
                eq(1L),
                eq(2L),
                eq(SystemConstants.ACTIVE),
                eq(SystemConstants.SYSTEM),
                any()
        );

        verify(productRepository).decreaseStockIfAvailable(
                eq(2L),
                eq(3L),
                eq(SystemConstants.ACTIVE),
                eq(SystemConstants.SYSTEM),
                any()
        );

        verify(sellerOrderService).createSellerOrdersForCheckout(
                any(Order.class), anyList()
        );

        verify(cartService).deleteProductFromCart(55L, 1L);
        verify(cartService).deleteProductFromCart(55L, 2L);
        verify(orderItemRepository).saveAll(anyList());
    }

    @Test
    void placeOrderForSingleProductCreatesOrderAndRemovesProductFromCart() {
        Long productId = 7L;
        Product product = product(productId);
        CartItem cartItem =
                cartItem(product, new BigDecimal("9.99"), 4);

        Cart cart = cart(88L, cartItem);

        Order savedOrder = new Order();
        OrderDTO orderDTO = new OrderDTO();
        OrderItemDTO orderItemDTO = new OrderItemDTO();

        stubCustomerEmail();
        stubAddressLookup();
        stubAuditUser();

        when(productRepository.findByProductIdAndStatus(
                productId, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(product));

        when(cartRepository.findByEmailId(CUSTOMER_EMAIL))
                .thenReturn(cart);

        when(cartItemRepository.findByProductIdAndCardId(productId, 88L))
                .thenReturn(Optional.of(cartItem));

        when(productRepository.decreaseStockIfAvailable(
                eq(productId),
                eq(4L),
                eq(SystemConstants.ACTIVE),
                eq(SystemConstants.SYSTEM),
                any()))
                .thenReturn(1);

        when(paymentRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        when(orderItemRepository.save(any(OrderItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(modelMapper.map(savedOrder, OrderDTO.class))
                .thenReturn(orderDTO);

        when(modelMapper.map(
                any(OrderItem.class), eq(OrderItemDTO.class)))
                .thenReturn(orderItemDTO);

        OrderDTO result =
                orderService.placeOrder(orderRequest, productId);

        assertSame(orderDTO, result);

        ArgumentCaptor<Order> orderCaptor =
                ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).save(orderCaptor.capture());

        assertEquals(
                new BigDecimal("39.96"),
                orderCaptor.getValue().getTotalAmount()
        );

        verify(productRepository).decreaseStockIfAvailable(
                eq(productId),
                eq(4L),
                eq(SystemConstants.ACTIVE),
                eq(SystemConstants.SYSTEM),
                any()
        );

        verify(orderItemRepository).save(any(OrderItem.class));

        verify(sellerOrderService).createSellerOrdersForCheckout(
                any(Order.class), anyList()
        );

        verify(cartService).deleteProductFromCart(88L, productId);
    }

    @Test
    void placeOrderThrowsWhenCartDoesNotExist() {
        stubCustomerEmail();

        when(cartRepository.findByEmailId(CUSTOMER_EMAIL))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.placeOrder(orderRequest)
        );

        verify(addressRepository, never())
                .findByIdAndEmailId(any(), any());
        verify(paymentRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderThrowsWhenCartIsEmpty() {
        Cart cart = cart(55L);

        stubCustomerEmail();
        stubAddressLookup();

        when(cartRepository.findByEmailId(CUSTOMER_EMAIL))
                .thenReturn(cart);

        EcommerceException exception = assertThrows(
                EcommerceException.class,
                () -> orderService.placeOrder(orderRequest)
        );

        assertEquals("Cart is Empty!", exception.getMessage());

        verify(paymentRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
        verifyStockWasNotDecreased();
    }

    @Test
    void placeOrderThrowsWhenCartItemQuantityIsInvalid() {
        Product product = product(3L);
        CartItem invalidItem =
                cartItem(product, new BigDecimal("4.00"), 0);

        Cart cart = cart(55L, invalidItem);

        stubCustomerEmail();
        stubAddressLookup();

        when(cartRepository.findByEmailId(CUSTOMER_EMAIL))
                .thenReturn(cart);

        assertThrows(
                EcommerceException.class,
                () -> orderService.placeOrder(orderRequest)
        );

        verifyStockWasNotDecreased();
        verify(paymentRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrderThrowsWhenStockCannotBeDecreased() {
        Long productId = 9L;
        Product product = product(productId);
        CartItem item =
                cartItem(product, new BigDecimal("3.00"), 2);

        Cart cart = cart(55L, item);

        stubCustomerEmail();
        stubAddressLookup();

        when(cartRepository.findByEmailId(CUSTOMER_EMAIL))
                .thenReturn(cart);

        when(productRepository.decreaseStockIfAvailable(
                eq(productId),
                eq(2L),
                eq(SystemConstants.ACTIVE),
                eq(SystemConstants.SYSTEM),
                any()))
                .thenReturn(0);

        assertThrows(
                EcommerceException.class,
                () -> orderService.placeOrder(orderRequest)
        );

        verify(paymentRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void singleProductOrderThrowsWhenProductIsInactiveOrMissing() {
        Long productId = 11L;

        when(productRepository.findByProductIdAndStatus(
                productId, SystemConstants.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.placeOrder(orderRequest, productId)
        );

        verify(cartRepository, never()).findByEmailId(any());
        verify(paymentRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void singleProductOrderThrowsWhenProductIsNotInCart() {
        Long productId = 12L;
        Cart cart = cart(99L);

        stubCustomerEmail();
        stubAddressLookup();

        when(productRepository.findByProductIdAndStatus(
                productId, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(product(productId)));

        when(cartRepository.findByEmailId(CUSTOMER_EMAIL))
                .thenReturn(cart);

        when(cartItemRepository.findByProductIdAndCardId(productId, 99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.placeOrder(orderRequest, productId)
        );

        // Confirms execution reached the intended missing-cart-item check.
        verify(cartItemRepository)
                .findByProductIdAndCardId(productId, 99L);

        verifyStockWasNotDecreased();
        verify(paymentRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    private void stubCustomerEmail() {
        when(authUtil.loggedInEmailId()).thenReturn(CUSTOMER_EMAIL);
    }

    private void stubAddressLookup() {
        when(addressRepository.findByIdAndEmailId(
                ADDRESS_ID, CUSTOMER_EMAIL))
                .thenReturn(Optional.of(address));
    }

    private void stubAuditUser() {
        when(authUtil.loggedInUser()).thenReturn(user);
    }

    private void verifyStockWasNotDecreased() {
        verify(productRepository, never()).decreaseStockIfAvailable(
                anyLong(),
                anyLong(),
                anyChar(),
                anyString(),
                any()
        );
    }

    private Product product(Long productId) {
        Product product = new Product();
        product.setProductId(productId);
        return product;
    }

    private CartItem cartItem(
            Product product,
            BigDecimal price,
            int quantity
    ) {
        CartItem item = new CartItem();
        item.setProduct(product);
        item.setProductPrice(price);
        item.setQuantity(quantity);
        return item;
    }

    private Cart cart(Long cartId, CartItem... items) {
        Cart cart = new Cart();
        cart.setCartId(cartId);
        cart.setCartItems(new ArrayList<>(List.of(items)));
        return cart;
    }
}