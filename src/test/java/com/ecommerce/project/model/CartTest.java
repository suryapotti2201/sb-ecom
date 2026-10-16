package com.ecommerce.project.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    @Test
    void noArgsConstructorInitializesDefaultValues() {
        Cart cart = new Cart();

        assertNull(cart.getCartId());
        assertNull(cart.getUser());
        assertNotNull(cart.getCartItems());
        assertTrue(cart.getCartItems().isEmpty());
        assertEquals(BigDecimal.valueOf(0.0), cart.getTotalPrice());
        assertNull(cart.getCreatedBy());
        assertNull(cart.getCreatedOn());
        assertNull(cart.getLastUpdatedBy());
        assertNull(cart.getLastUpdatedOn());
    }

    @Test
    void allArgsConstructorSetsAllProperties() {
        Long cartId = 10L;
        User user = new User();
        List<CartItem> cartItems = new ArrayList<>();
        CartItem cartItem = new CartItem();
        cartItems.add(cartItem);
        BigDecimal totalPrice = new BigDecimal("149.99");
        LocalDateTime createdOn = LocalDateTime.of(2025, 1, 10, 12, 30);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 1, 11, 14, 45);

        Cart cart = new Cart(
                cartId,
                user,
                cartItems,
                totalPrice,
                "creator",
                createdOn,
                "updater",
                lastUpdatedOn
        );

        assertEquals(cartId, cart.getCartId());
        assertSame(user, cart.getUser());
        assertSame(cartItems, cart.getCartItems());
        assertEquals(totalPrice, cart.getTotalPrice());
        assertEquals("creator", cart.getCreatedBy());
        assertEquals(createdOn, cart.getCreatedOn());
        assertEquals("updater", cart.getLastUpdatedBy());
        assertEquals(lastUpdatedOn, cart.getLastUpdatedOn());
    }

    @Test
    void settersAndGettersUpdateAllProperties() {
        Cart cart = new Cart();
        Long cartId = 25L;
        User user = new User();
        List<CartItem> cartItems = new ArrayList<>();
        BigDecimal totalPrice = new BigDecimal("250.50");
        LocalDateTime createdOn = LocalDateTime.of(2025, 2, 1, 9, 15);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 2, 2, 10, 20);

        cart.setCartId(cartId);
        cart.setUser(user);
        cart.setCartItems(cartItems);
        cart.setTotalPrice(totalPrice);
        cart.setCreatedBy("created-by");
        cart.setCreatedOn(createdOn);
        cart.setLastUpdatedBy("updated-by");
        cart.setLastUpdatedOn(lastUpdatedOn);

        assertEquals(cartId, cart.getCartId());
        assertSame(user, cart.getUser());
        assertSame(cartItems, cart.getCartItems());
        assertEquals(totalPrice, cart.getTotalPrice());
        assertEquals("created-by", cart.getCreatedBy());
        assertEquals(createdOn, cart.getCreatedOn());
        assertEquals("updated-by", cart.getLastUpdatedBy());
        assertEquals(lastUpdatedOn, cart.getLastUpdatedOn());
    }

    @Test
    void cartItemsCanBeModifiedThroughGetter() {
        Cart cart = new Cart();
        CartItem cartItem = new CartItem();

        cart.getCartItems().add(cartItem);

        assertEquals(1, cart.getCartItems().size());
        assertSame(cartItem, cart.getCartItems().get(0));
    }

    @Test
    void settersAcceptNullValues() {
        Cart cart = new Cart();

        cart.setCartId(null);
        cart.setUser(null);
        cart.setCartItems(null);
        cart.setTotalPrice(null);
        cart.setCreatedBy(null);
        cart.setCreatedOn(null);
        cart.setLastUpdatedBy(null);
        cart.setLastUpdatedOn(null);

        assertNull(cart.getCartId());
        assertNull(cart.getUser());
        assertNull(cart.getCartItems());
        assertNull(cart.getTotalPrice());
        assertNull(cart.getCreatedBy());
        assertNull(cart.getCreatedOn());
        assertNull(cart.getLastUpdatedBy());
        assertNull(cart.getLastUpdatedOn());
    }

    @Test
    void equalCartsHaveSameHashCode() {
        Cart first = new Cart();
        Cart second = new Cart();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void cartsWithDifferentPropertiesAreNotEqual() {
        Cart first = new Cart();
        Cart second = new Cart();
        second.setCartId(1L);

        assertNotEquals(first, second);
    }

    @Test
    void toStringContainsCartState() {
        Cart cart = new Cart();
        cart.setCartId(7L);
        cart.setTotalPrice(new BigDecimal("19.99"));

        String representation = cart.toString();

        assertNotNull(representation);
        assertTrue(representation.contains("cartId=7"));
        assertTrue(representation.contains("totalPrice=19.99"));
    }
}
