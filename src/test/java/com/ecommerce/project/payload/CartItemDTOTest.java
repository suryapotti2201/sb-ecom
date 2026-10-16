package com.ecommerce.project.payload;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CartItemDTOTest {

    @Test
    void noArgsConstructorInitializesFieldsToNull() {
        CartItemDTO cartItem = new CartItemDTO();

        assertAll(
                () -> assertNull(cartItem.getCartItemId()),
                () -> assertNull(cartItem.getCartDTO()),
                () -> assertNull(cartItem.getProductDTO()),
                () -> assertNull(cartItem.getQuantity()),
                () -> assertNull(cartItem.getProductPrice()),
                () -> assertNull(cartItem.getDiscount())
        );
    }

    @Test
    void allArgsConstructorPopulatesAllFields() {
        CartDTO cart = new CartDTO();
        ProductDTO product = new ProductDTO(
                "Laptop",
                "laptop.jpg",
                new BigDecimal("999.99"),
                new BigDecimal("10.00"),
                new BigDecimal("899.99"),
                5L,
                'A'
        );
        BigDecimal productPrice = new BigDecimal("999.99");
        BigDecimal discount = new BigDecimal("10.00");

        CartItemDTO cartItem = new CartItemDTO(
                1L,
                cart,
                product,
                2L,
                productPrice,
                discount
        );

        assertAll(
                () -> assertEquals(1L, cartItem.getCartItemId()),
                () -> assertSame(cart, cartItem.getCartDTO()),
                () -> assertSame(product, cartItem.getProductDTO()),
                () -> assertEquals(2L, cartItem.getQuantity()),
                () -> assertEquals(productPrice, cartItem.getProductPrice()),
                () -> assertEquals(discount, cartItem.getDiscount())
        );
    }

    @Test
    void settersUpdateAllFields() {
        CartItemDTO cartItem = new CartItemDTO();
        CartDTO cart = new CartDTO();
        ProductDTO product = new ProductDTO(
                "Phone",
                "phone.jpg",
                new BigDecimal("499.99"),
                BigDecimal.ZERO,
                new BigDecimal("499.99"),
                10L,
                'A'
        );

        cartItem.setCartItemId(10L);
        cartItem.setCartDTO(cart);
        cartItem.setProductDTO(product);
        cartItem.setQuantity(3L);
        cartItem.setProductPrice(new BigDecimal("499.99"));
        cartItem.setDiscount(new BigDecimal("5.00"));

        assertAll(
                () -> assertEquals(10L, cartItem.getCartItemId()),
                () -> assertSame(cart, cartItem.getCartDTO()),
                () -> assertSame(product, cartItem.getProductDTO()),
                () -> assertEquals(3L, cartItem.getQuantity()),
                () -> assertEquals(new BigDecimal("499.99"), cartItem.getProductPrice()),
                () -> assertEquals(new BigDecimal("5.00"), cartItem.getDiscount())
        );
    }

    @Test
    void equalsAndHashCodeAreValueBased() {
        CartDTO cart = new CartDTO();
        ProductDTO product = new ProductDTO(
                "Tablet",
                "tablet.jpg",
                new BigDecimal("299.99"),
                new BigDecimal("15.00"),
                new BigDecimal("254.99"),
                4L,
                'A'
        );

        CartItemDTO first = new CartItemDTO(
                1L,
                cart,
                product,
                2L,
                new BigDecimal("299.99"),
                new BigDecimal("15.00")
        );
        CartItemDTO second = new CartItemDTO(
                1L,
                cart,
                product,
                2L,
                new BigDecimal("299.99"),
                new BigDecimal("15.00")
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, null);
        assertNotEquals(first, new Object());

        second.setQuantity(3L);
        assertNotEquals(first, second);
    }

    @Test
    void toStringContainsFieldValues() {
        CartItemDTO cartItem = new CartItemDTO(
                7L,
                null,
                null,
                4L,
                new BigDecimal("125.50"),
                new BigDecimal("12.50")
        );

        String text = cartItem.toString();

        assertAll(
                () -> assertTrue(text.contains("cartItemId=7")),
                () -> assertTrue(text.contains("quantity=4")),
                () -> assertTrue(text.contains("productPrice=125.50")),
                () -> assertTrue(text.contains("discount=12.50"))
        );
    }
}
