package com.ecommerce.project.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CartItemTest {

    @Test
    void noArgsConstructorInitializesAllFieldsToNull() {
        CartItem cartItem = new CartItem();

        assertAll(
                () -> assertNull(cartItem.getCartItemId()),
                () -> assertNull(cartItem.getCart()),
                () -> assertNull(cartItem.getProduct()),
                () -> assertNull(cartItem.getQuantity()),
                () -> assertNull(cartItem.getProductPrice()),
                () -> assertNull(cartItem.getDiscount()),
                () -> assertNull(cartItem.getCreatedBy()),
                () -> assertNull(cartItem.getCreatedOn()),
                () -> assertNull(cartItem.getLastUpdatedBy()),
                () -> assertNull(cartItem.getLastUpdatedOn())
        );
    }

    @Test
    void allArgsConstructorAndGettersExposeProvidedValues() {
        Cart cart = mock(Cart.class);
        Product product = mock(Product.class);
        BigDecimal productPrice = new BigDecimal("24.99");
        BigDecimal discount = new BigDecimal("2.50");
        LocalDateTime createdOn = LocalDateTime.of(2025, 1, 10, 12, 30);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 1, 11, 15, 45);

        CartItem cartItem = new CartItem(
                1L,
                cart,
                product,
                3,
                productPrice,
                discount,
                "creator",
                createdOn,
                "updater",
                lastUpdatedOn
        );

        assertAll(
                () -> assertEquals(1L, cartItem.getCartItemId()),
                () -> assertSame(cart, cartItem.getCart()),
                () -> assertSame(product, cartItem.getProduct()),
                () -> assertEquals(3, cartItem.getQuantity()),
                () -> assertEquals(productPrice, cartItem.getProductPrice()),
                () -> assertEquals(discount, cartItem.getDiscount()),
                () -> assertEquals("creator", cartItem.getCreatedBy()),
                () -> assertEquals(createdOn, cartItem.getCreatedOn()),
                () -> assertEquals("updater", cartItem.getLastUpdatedBy()),
                () -> assertEquals(lastUpdatedOn, cartItem.getLastUpdatedOn())
        );
    }

    @Test
    void settersUpdateAllFields() {
        CartItem cartItem = new CartItem();
        Cart cart = mock(Cart.class);
        Product product = mock(Product.class);
        BigDecimal productPrice = new BigDecimal("19.95");
        BigDecimal discount = new BigDecimal("1.25");
        LocalDateTime createdOn = LocalDateTime.of(2025, 2, 1, 10, 0);
        LocalDateTime lastUpdatedOn = LocalDateTime.of(2025, 2, 2, 11, 0);

        cartItem.setCartItemId(2L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(5);
        cartItem.setProductPrice(productPrice);
        cartItem.setDiscount(discount);
        cartItem.setCreatedBy("created-by");
        cartItem.setCreatedOn(createdOn);
        cartItem.setLastUpdatedBy("updated-by");
        cartItem.setLastUpdatedOn(lastUpdatedOn);

        assertAll(
                () -> assertEquals(2L, cartItem.getCartItemId()),
                () -> assertSame(cart, cartItem.getCart()),
                () -> assertSame(product, cartItem.getProduct()),
                () -> assertEquals(5, cartItem.getQuantity()),
                () -> assertEquals(productPrice, cartItem.getProductPrice()),
                () -> assertEquals(discount, cartItem.getDiscount()),
                () -> assertEquals("created-by", cartItem.getCreatedBy()),
                () -> assertEquals(createdOn, cartItem.getCreatedOn()),
                () -> assertEquals("updated-by", cartItem.getLastUpdatedBy()),
                () -> assertEquals(lastUpdatedOn, cartItem.getLastUpdatedOn())
        );
    }

    @Test
    void equalCartItemsHaveEqualHashCodes() {
        Cart cart = mock(Cart.class);
        Product product = mock(Product.class);
        LocalDateTime timestamp = LocalDateTime.of(2025, 3, 1, 9, 15);

        CartItem first = new CartItem(
                3L,
                cart,
                product,
                2,
                new BigDecimal("10.00"),
                new BigDecimal("0.50"),
                "user",
                timestamp,
                "user",
                timestamp
        );
        CartItem second = new CartItem(
                3L,
                cart,
                product,
                2,
                new BigDecimal("10.00"),
                new BigDecimal("0.50"),
                "user",
                timestamp,
                "user",
                timestamp
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(first, first);
        assertNotEquals(first, null);
        assertNotEquals(first, "not a cart item");
    }

    @Test
    void changingAFieldMakesCartItemsUnequal() {
        CartItem first = new CartItem();
        CartItem second = new CartItem();

        first.setQuantity(1);
        second.setQuantity(2);

        assertNotEquals(first, second);
    }

    @Test
    void toStringIncludesScalarStateButExcludesCartAndProduct() {
        CartItem cartItem = new CartItem();
        cartItem.setCartItemId(4L);
        cartItem.setQuantity(7);
        cartItem.setProductPrice(new BigDecimal("12.50"));
        cartItem.setCreatedBy("tester");

        String value = cartItem.toString();

        assertAll(
                () -> assertTrue(value.contains("cartItemId=4")),
                () -> assertTrue(value.contains("quantity=7")),
                () -> assertTrue(value.contains("productPrice=12.50")),
                () -> assertTrue(value.contains("createdBy=tester")),
                () -> assertFalse(value.contains("cart=")),
                () -> assertFalse(value.contains("product="))
        );
    }
}
