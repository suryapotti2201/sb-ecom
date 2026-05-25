package com.ecommerce.project.service;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.payload.CartDTO;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCart();

    String deleteProductFromCart(Long productId);

    CartDTO updateCartProduct(Long productId, int quantity);

    CartDTO convertCartToCartDTO(Cart cart);

    void updateProductInCarts(Long cartId, Long productId);

    void deleteProductFromCart(Long cartId, Long productId);
}
