package com.ecommerce.project.controller;

import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private CartDTO cartDTO;

    @InjectMocks
    private CartController cartController;

    @Test
    void addProductToCartReturnsCreatedCart() {
        Long productId = 1L;
        Integer quantity = 2;

        when(cartService.addProductToCart(productId, quantity)).thenReturn(cartDTO);

        ResponseEntity<CartDTO> response =
                cartController.addProductToCart(productId, quantity);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(cartDTO, response.getBody());
        verify(cartService).addProductToCart(productId, quantity);
    }

    @Test
    void addProductToCartPropagatesServiceException() {
        Long productId = 1L;
        Integer quantity = 2;
        RuntimeException exception = new RuntimeException("Unable to add product");

        when(cartService.addProductToCart(productId, quantity)).thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> cartController.addProductToCart(productId, quantity)
        );

        assertSame(exception, thrown);
        verify(cartService).addProductToCart(productId, quantity);
    }

    @Test
    void getCartsReturnsAllCartsWithOkStatus() {
        List<CartDTO> carts = List.of(cartDTO);

        when(cartService.getAllCarts()).thenReturn(carts);

        ResponseEntity<List<CartDTO>> response = cartController.getCarts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(carts, response.getBody());
        verify(cartService).getAllCarts();
    }

    @Test
    void getCardByIdReturnsCurrentUsersCartWithOkStatus() {
        when(cartService.getCart()).thenReturn(cartDTO);

        ResponseEntity<CartDTO> response = cartController.getCardById();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(cartDTO, response.getBody());
        verify(cartService).getCart();
    }

    @Test
    void deleteProductFromCartReturnsServiceStatusWithOkStatus() {
        Long productId = 1L;
        String status = "Product removed from cart";

        when(cartService.deleteProductFromCart(productId)).thenReturn(status);

        ResponseEntity<String> response =
                cartController.deleteProductFromCart(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(status, response.getBody());
        verify(cartService).deleteProductFromCart(productId);
    }

    @Test
    void updateCartProductReturnsUpdatedCartWithOkStatus() {
        Long productId = 1L;
        Integer quantity = 3;

        when(cartService.updateCartProduct(productId, quantity)).thenReturn(cartDTO);

        ResponseEntity<CartDTO> response =
                cartController.updateCartProduct(productId, quantity);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(cartDTO, response.getBody());
        verify(cartService).updateCartProduct(productId, quantity);
    }

    @Test
    void updateCartProductPropagatesServiceException() {
        Long productId = 1L;
        Integer quantity = 0;
        RuntimeException exception = new RuntimeException("Invalid quantity");

        when(cartService.updateCartProduct(productId, quantity)).thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> cartController.updateCartProduct(productId, quantity)
        );

        assertSame(exception, thrown);
        verify(cartService).updateCartProduct(productId, quantity);
    }
}
