package com.ecommerce.project.payload;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartDTOTest {

    @Test
    void noArgsConstructorShouldInitializeDefaultValues() {
        CartDTO cart = new CartDTO();

        assertNull(cart.getCartId());
        assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
        assertNotNull(cart.getProducts());
        assertTrue(cart.getProducts().isEmpty());
    }

    @Test
    void noArgsConstructorShouldCreateIndependentProductLists() {
        CartDTO firstCart = new CartDTO();
        CartDTO secondCart = new CartDTO();

        firstCart.getProducts().add(new ProductDTO());

        assertEquals(1, firstCart.getProducts().size());
        assertTrue(secondCart.getProducts().isEmpty());
        assertNotSame(firstCart.getProducts(), secondCart.getProducts());
    }

    @Test
    void allArgsConstructorShouldAssignAllFields() {
        Long cartId = 42L;
        BigDecimal totalPrice = new BigDecimal("125.50");
        List<ProductDTO> products = new ArrayList<>();
        ProductDTO product = new ProductDTO();
        products.add(product);

        CartDTO cart = new CartDTO(cartId, totalPrice, products);

        assertEquals(cartId, cart.getCartId());
        assertEquals(totalPrice, cart.getTotalPrice());
        assertSame(products, cart.getProducts());
        assertEquals(List.of(product), cart.getProducts());
    }

    @Test
    void settersAndGettersShouldUpdateAndReturnValues() {
        CartDTO cart = new CartDTO();
        Long cartId = 7L;
        BigDecimal totalPrice = new BigDecimal("99.99");
        List<ProductDTO> products = List.of(new ProductDTO());

        cart.setCartId(cartId);
        cart.setTotalPrice(totalPrice);
        cart.setProducts(products);

        assertEquals(cartId, cart.getCartId());
        assertEquals(totalPrice, cart.getTotalPrice());
        assertSame(products, cart.getProducts());
    }

    @Test
    void settersShouldSupportNullValues() {
        CartDTO cart = new CartDTO();

        cart.setCartId(null);
        cart.setTotalPrice(null);
        cart.setProducts(null);

        assertNull(cart.getCartId());
        assertNull(cart.getTotalPrice());
        assertNull(cart.getProducts());
    }

    @Test
    void equalCartsShouldBeEqualAndHaveSameHashCode() {
        List<ProductDTO> products = List.of(new ProductDTO());

        CartDTO firstCart = new CartDTO(1L, new BigDecimal("10.00"), products);
        CartDTO secondCart = new CartDTO(1L, new BigDecimal("10.00"), products);

        assertEquals(firstCart, secondCart);
        assertEquals(firstCart.hashCode(), secondCart.hashCode());
    }

    @Test
    void cartsWithDifferentValuesShouldNotBeEqual() {
        CartDTO firstCart = new CartDTO(1L, new BigDecimal("10.00"), new ArrayList<>());
        CartDTO differentCart = new CartDTO(2L, new BigDecimal("10.00"), new ArrayList<>());

        assertNotEquals(firstCart, differentCart);
        assertNotEquals(firstCart, null);
        assertNotEquals(firstCart, "not a cart");
    }

    @Test
    void toStringShouldContainCartState() {
        CartDTO cart = new CartDTO(3L, new BigDecimal("25.00"), new ArrayList<>());

        String result = cart.toString();

        assertTrue(result.contains("cartId=3"));
        assertTrue(result.contains("totalPrice=25.00"));
        assertTrue(result.contains("products=[]"));
    }
}
