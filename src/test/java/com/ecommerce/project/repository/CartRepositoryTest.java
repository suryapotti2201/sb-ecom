package com.ecommerce.project.repository;

import com.ecommerce.project.model.Cart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.Query;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartRepositoryTest {

    @Mock
    private CartRepository cartRepository;

    @Test
    void findByEmailId_returnsCartForMatchingEmail() {
        String email = "customer@example.com";
        Cart expectedCart = org.mockito.Mockito.mock(Cart.class);

        when(cartRepository.findByEmailId(email)).thenReturn(expectedCart);

        Cart actualCart = cartRepository.findByEmailId(email);

        assertSame(expectedCart, actualCart);
        verify(cartRepository).findByEmailId(email);
    }

    @Test
    void findByEmailId_returnsNullWhenNoCartExists() {
        String email = "unknown@example.com";

        when(cartRepository.findByEmailId(email)).thenReturn(null);

        Cart actualCart = cartRepository.findByEmailId(email);

        assertEquals(null, actualCart);
        verify(cartRepository).findByEmailId(email);
    }

    @Test
    void findByProductId_returnsMatchingCarts() {
        Long productId = 42L;
        Cart firstCart = org.mockito.Mockito.mock(Cart.class);
        Cart secondCart = org.mockito.Mockito.mock(Cart.class);
        List<Cart> expectedCarts = List.of(firstCart, secondCart);

        when(cartRepository.findByProductId(productId)).thenReturn(expectedCarts);

        List<Cart> actualCarts = cartRepository.findByProductId(productId);

        assertSame(expectedCarts, actualCarts);
        verify(cartRepository).findByProductId(productId);
    }

    @Test
    void findByProductId_returnsEmptyListWhenProductIsNotInAnyCart() {
        Long productId = 999L;
        List<Cart> expectedCarts = Collections.emptyList();

        when(cartRepository.findByProductId(productId)).thenReturn(expectedCarts);

        List<Cart> actualCarts = cartRepository.findByProductId(productId);

        assertSame(expectedCarts, actualCarts);
        verify(cartRepository).findByProductId(productId);
    }

    @Test
    void repositoryMethodsDeclareExpectedQueries() throws NoSuchMethodException {
        Method findByEmailId = CartRepository.class.getMethod("findByEmailId", String.class);
        Method findByProductId = CartRepository.class.getMethod("findByProductId", Long.class);

        assertEquals(
                "SELECT c FROM Cart c WHERE c.user.emailId = ?1",
                findByEmailId.getAnnotation(Query.class).value()
        );
        assertEquals(
                "SELECT c FROM Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product p WHERE p.id = ?1",
                findByProductId.getAnnotation(Query.class).value()
        );
    }
}
