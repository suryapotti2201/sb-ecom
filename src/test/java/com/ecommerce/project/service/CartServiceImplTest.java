package com.ecommerce.project.service;

import com.ecommerce.project.constants.SystemConstants;
import com.ecommerce.project.exception.EcommerceException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Cart cart;
    private Product product;
    private CartItem cartItem;
    private CartDTO cartDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserName("customer");

        cart = new Cart();
        cart.setCartId(10L);
        cart.setTotalPrice(new BigDecimal("20.00"));
        cart.setCartItems(new ArrayList<>());

        product = new Product();
        product.setProductId(5L);
        product.setProductName("Phone");
        product.setQuantity(10L);
        product.setSpecialPrice(new BigDecimal("15.00"));
        product.setDiscount(new BigDecimal("2.00"));

        cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setProductPrice(new BigDecimal("10.00"));

        cartDTO = new CartDTO();

        lenient().when(authUtil.loggedInEmailId()).thenReturn("customer@example.com");
        lenient().when(authUtil.loggedInUser()).thenReturn(user);
        lenient().when(modelMapper.map(any(Cart.class), eq(CartDTO.class))).thenReturn(cartDTO);
        lenient().when(modelMapper.map(any(Product.class), eq(ProductDTO.class)))
                .thenReturn(new ProductDTO());
    }

    @Test
    void addProductToCart_addsItemAndUpdatesCartTotal() {
        when(cartRepository.findByEmailId("customer@example.com")).thenReturn(cart);
        when(productRepository.findByProductIdAndStatus(5L, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(product));
        when(cartItemRepository.findByProductIdAndCardId(5L, 10L))
                .thenReturn(Optional.empty());
        when(cartRepository.save(cart)).thenReturn(cart);

        CartDTO result = cartService.addProductToCart(5L, 2);

        assertSame(cartDTO, result);
        assertEquals(new BigDecimal("50.00"), cart.getTotalPrice());

        ArgumentCaptor<CartItem> itemCaptor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository).save(itemCaptor.capture());
        CartItem savedItem = itemCaptor.getValue();
        assertSame(cart, savedItem.getCart());
        assertSame(product, savedItem.getProduct());
        assertEquals(2, savedItem.getQuantity());
        assertEquals(product.getSpecialPrice(), savedItem.getProductPrice());
        assertEquals("customer", savedItem.getCreatedBy());
        verify(cartRepository).save(cart);
    }

    @Test
    void addProductToCart_createsCartWhenUserHasNoCart() {
        when(cartRepository.findByEmailId("customer@example.com")).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart saved = invocation.getArgument(0);
            saved.setCartId(10L);
            return saved;
        });
        when(productRepository.findByProductIdAndStatus(5L, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(product));
        when(cartItemRepository.findByProductIdAndCardId(5L, 10L))
                .thenReturn(Optional.empty());

        cartService.addProductToCart(5L, 1);

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository, times(2)).save(cartCaptor.capture());
        Cart createdCart = cartCaptor.getAllValues().get(0);
        assertEquals(new BigDecimal("15.00"), createdCart.getTotalPrice());
        assertSame(user, createdCart.getUser());
        assertEquals("customer", createdCart.getCreatedBy());
    }

    @Test
    void addProductToCart_rejectsDuplicateProduct() {
        when(cartRepository.findByEmailId("customer@example.com")).thenReturn(cart);
        when(productRepository.findByProductIdAndStatus(5L, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(product));
        when(cartItemRepository.findByProductIdAndCardId(5L, 10L))
                .thenReturn(Optional.of(cartItem));

        assertThrows(EcommerceException.class,
                () -> cartService.addProductToCart(5L, 1));

        verify(cartItemRepository, never()).save(any());
        verify(cartRepository, never()).save(cart);
    }

    @Test
    void addProductToCart_rejectsUnavailableProduct() {
        product.setQuantity(0L);
        when(cartRepository.findByEmailId("customer@example.com")).thenReturn(cart);
        when(productRepository.findByProductIdAndStatus(5L, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(product));
        when(cartItemRepository.findByProductIdAndCardId(5L, 10L))
                .thenReturn(Optional.empty());

        assertThrows(EcommerceException.class,
                () -> cartService.addProductToCart(5L, 1));
    }

    @Test
    void addProductToCart_rejectsQuantityGreaterThanStock() {
        product.setQuantity(1L);
        when(cartRepository.findByEmailId("customer@example.com")).thenReturn(cart);
        when(productRepository.findByProductIdAndStatus(5L, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(product));
        when(cartItemRepository.findByProductIdAndCardId(5L, 10L))
                .thenReturn(Optional.empty());

        assertThrows(EcommerceException.class,
                () -> cartService.addProductToCart(5L, 2));
    }

    @Test
    void addProductToCart_rejectsNonPositiveQuantity() {
        when(cartRepository.findByEmailId("customer@example.com")).thenReturn(cart);
        when(productRepository.findByProductIdAndStatus(5L, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(product));
        when(cartItemRepository.findByProductIdAndCardId(5L, 10L))
                .thenReturn(Optional.empty());

        assertThrows(EcommerceException.class,
                () -> cartService.addProductToCart(5L, 0));
    }

    @Test
    void addProductToCart_throwsWhenProductDoesNotExist() {
        when(cartRepository.findByEmailId("customer@example.com")).thenReturn(cart);
        when(productRepository.findByProductIdAndStatus(5L, SystemConstants.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> cartService.addProductToCart(5L, 1));
    }

    @Test
    void getAllCarts_returnsMappedCarts() {
        Cart secondCart = new Cart();
        secondCart.setCartItems(new ArrayList<>());

        when(cartRepository.findAll()).thenReturn(List.of(cart, secondCart));
        when(modelMapper.map(cart, CartDTO.class)).thenReturn(cartDTO);
        when(modelMapper.map(secondCart, CartDTO.class)).thenReturn(new CartDTO());

        List<CartDTO> result = cartService.getAllCarts();

        assertEquals(2, result.size());
        verify(cartRepository).findAll();
    }

    @Test
    void getAllCarts_throwsWhenNoCartsExist() {
        when(cartRepository.findAll()).thenReturn(List.of());

        assertThrows(EcommerceException.class, () -> cartService.getAllCarts());
    }

    @Test
    void getCart_returnsLoggedInUsersCart() {
        when(cartRepository.findByEmailId("customer@example.com")).thenReturn(cart);

        assertSame(cartDTO, cartService.getCart());

        verify(authUtil).loggedInEmailId();
    }

    @Test
    void getCart_throwsWhenCartDoesNotExist() {
        when(cartRepository.findByEmailId("customer@example.com")).thenReturn(null);

        assertThrows(EcommerceException.class, () -> cartService.getCart());
    }

    @Test
    void deleteProductFromCart_removesItemAndUpdatesTotal() {
        cart.setTotalPrice(new BigDecimal("30.00"));
        cartItem.setQuantity(2);
        cartItem.setProductPrice(new BigDecimal("10.00"));

        when(cartRepository.findByEmailId("customer@example.com")).thenReturn(cart);
        when(productRepository.findByProductIdAndStatus(5L, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(product));
        when(cartItemRepository.findByProductIdAndCardId(5L, 10L))
                .thenReturn(Optional.of(cartItem));
        when(cartRepository.save(cart)).thenReturn(cart);

        String result = cartService.deleteProductFromCart(5L);

        assertEquals("Product (Phone) removed from the cart!", result);
        assertEquals(new BigDecimal("10.00"), cart.getTotalPrice());
        verify(cartItemRepository).deleteCartItemByProductIdAndCartId(10L, 5L);
        verify(cartRepository).save(cart);
    }

    @Test
    void deleteProductFromCart_throwsWhenCartDoesNotExist() {
        when(cartRepository.findByEmailId("customer@example.com")).thenReturn(null);

        assertThrows(EcommerceException.class,
                () -> cartService.deleteProductFromCart(5L));

        verifyNoInteractions(productRepository, cartItemRepository);
    }

    @Test
    void updateCartProduct_incrementsQuantityAndTotal() {
        cart.setTotalPrice(new BigDecimal("20.00"));
        cartItem.setQuantity(2);
        cartItem.setProductPrice(new BigDecimal("10.00"));

        when(productRepository.findByProductIdAndStatus(5L, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(product));
        when(cartRepository.findByEmailId("customer@example.com")).thenReturn(cart);
        when(cartItemRepository.findByProductIdAndCardId(5L, 10L))
                .thenReturn(Optional.of(cartItem));
        when(cartRepository.save(cart)).thenReturn(cart);

        CartDTO result = cartService.updateCartProduct(5L, 1);

        assertSame(cartDTO, result);
        assertEquals(3, cartItem.getQuantity());
        assertEquals(new BigDecimal("30.00"), cart.getTotalPrice());
        verify(cartItemRepository).save(cartItem);
        verify(cartRepository).save(cart);
    }

    @Test
    void updateCartProduct_decrementsToZeroAndDeletesItem() {
        cartItem.setQuantity(1);
        cartItem.setProductPrice(new BigDecimal("10.00"));

        when(productRepository.findByProductIdAndStatus(5L, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(product));
        when(cartRepository.findByEmailId("customer@example.com")).thenReturn(cart);
        when(cartItemRepository.findByProductIdAndCardId(5L, 10L))
                .thenReturn(Optional.of(cartItem));
        when(cartRepository.save(cart)).thenReturn(cart);

        cartService.updateCartProduct(5L, -1);

        verify(cartItemRepository).deleteCartItemByProductIdAndCartId(10L, 5L);
        verify(cartItemRepository, never()).save(any(CartItem.class));
        assertEquals(new BigDecimal("10.00"), cart.getTotalPrice());
    }

    @Test
    void updateCartProduct_rejectsInvalidAdjustment() {
        assertThrows(EcommerceException.class,
                () -> cartService.updateCartProduct(5L, 2));

        verifyNoInteractions(productRepository, cartRepository, cartItemRepository);
    }

    @Test
    void updateCartProduct_rejectsInsufficientStock() {
        product.setQuantity(2L);
        cartItem.setQuantity(2);

        when(productRepository.findByProductIdAndStatus(5L, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(product));
        when(cartRepository.findByEmailId("customer@example.com")).thenReturn(cart);
        when(cartItemRepository.findByProductIdAndCardId(5L, 10L))
                .thenReturn(Optional.of(cartItem));

        assertThrows(EcommerceException.class,
                () -> cartService.updateCartProduct(5L, 1));

        verify(cartItemRepository, never()).save(any());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void convertCartToCartDTO_mapsProductsAndSetsQuantities() {
        ProductDTO productDTO = new ProductDTO();
        cartItem.setQuantity(3);
        cart.setCartItems(List.of(cartItem));

        when(modelMapper.map(cart, CartDTO.class)).thenReturn(cartDTO);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        CartDTO result = cartService.convertCartToCartDTO(cart);

        assertSame(cartDTO, result);
        assertNotNull(result.getProducts());
        assertEquals(1, result.getProducts().size());
        assertEquals(Long.valueOf(3), result.getProducts().get(0).getQuantity());
    }

    @Test
    void updateProductInCarts_updatesPriceAndCartTotal() {
        cart.setTotalPrice(new BigDecimal("20.00"));
        cartItem.setQuantity(2);
        cartItem.setProductPrice(new BigDecimal("10.00"));
        product.setSpecialPrice(new BigDecimal("12.50"));

        when(cartRepository.findById(10L)).thenReturn(Optional.of(cart));
        when(productRepository.findByProductIdAndStatus(5L, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(product));
        when(cartItemRepository.findByProductIdAndCardId(5L, 10L))
                .thenReturn(Optional.of(cartItem));

        cartService.updateProductInCarts(10L, 5L);

        assertEquals(new BigDecimal("25.00"), cart.getTotalPrice());
        assertEquals(new BigDecimal("12.50"), cartItem.getProductPrice());
        assertEquals(SystemConstants.SYSTEM, cart.getLastUpdatedBy());
        assertEquals(SystemConstants.SYSTEM, cartItem.getLastUpdatedBy());
        verify(cartRepository).save(cart);
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void updateProductInCarts_throwsWhenCartDoesNotExist() {
        when(cartRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> cartService.updateProductInCarts(10L, 5L));

        verifyNoInteractions(productRepository, cartItemRepository);
    }

    @Test
    void deleteProductFromCartByCartId_removesItemAndUpdatesTotal() {
        cart.setTotalPrice(new BigDecimal("30.00"));
        cartItem.setQuantity(2);
        cartItem.setProductPrice(new BigDecimal("10.00"));

        when(cartRepository.findById(10L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByProductIdAndCardId(5L, 10L))
                .thenReturn(Optional.of(cartItem));

        cartService.deleteProductFromCart(10L, 5L);

        assertEquals(new BigDecimal("10.00"), cart.getTotalPrice());
        assertEquals(SystemConstants.SYSTEM, cart.getLastUpdatedBy());
        verify(cartItemRepository).deleteCartItemByProductIdAndCartId(10L, 5L);
        verify(cartRepository).save(cart);
    }

    @Test
    void deleteProductFromCartByCartId_throwsWhenItemDoesNotExist() {
        when(cartRepository.findById(10L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByProductIdAndCardId(5L, 10L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> cartService.deleteProductFromCart(10L, 5L));

        verify(cartItemRepository, never())
                .deleteCartItemByProductIdAndCartId(anyLong(), anyLong());
        verify(cartRepository, never()).save(any(Cart.class));
    }
}
