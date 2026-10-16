package com.ecommerce.project.repository;

import com.ecommerce.project.model.CartItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartItemRepositoryTest {

    @Mock
    private CartItemRepository repository;

    @Test
    void findByProductIdAndCardId_returnsMatchingCartItem() {
        Long productId = 10L;
        Long cartId = 20L;
        CartItem cartItem = org.mockito.Mockito.mock(CartItem.class);

        when(repository.findByProductIdAndCardId(productId, cartId))
                .thenReturn(Optional.of(cartItem));

        Optional<CartItem> result = repository.findByProductIdAndCardId(productId, cartId);

        assertTrue(result.isPresent());
        assertSame(cartItem, result.get());
        verify(repository).findByProductIdAndCardId(productId, cartId);
    }

    @Test
    void findByProductIdAndCardId_returnsEmptyWhenNoMatchingCartItemExists() {
        Long productId = 10L;
        Long cartId = 20L;

        when(repository.findByProductIdAndCardId(productId, cartId))
                .thenReturn(Optional.empty());

        Optional<CartItem> result = repository.findByProductIdAndCardId(productId, cartId);

        assertEquals(Optional.empty(), result);
        verify(repository).findByProductIdAndCardId(productId, cartId);
    }

    @Test
    void deleteCartItemByProductIdAndCartId_deletesUsingCartAndProductIds() {
        Long cartId = 20L;
        Long productId = 10L;

        repository.deleteCartItemByProductIdAndCartId(cartId, productId);

        verify(repository).deleteCartItemByProductIdAndCartId(cartId, productId);
    }

    @Test
    void findByProductIdAndCardId_declaresExpectedQuery() throws NoSuchMethodException {
        Query query = CartItemRepository.class
                .getMethod("findByProductIdAndCardId", Long.class, Long.class)
                .getAnnotation(Query.class);

        assertEquals(
                "SELECT ci FROM CartItem ci WHERE ci.product.productId = ?1 and ci.cart.cartId = ?2",
                query.value()
        );
    }

    @Test
    void deleteCartItemByProductIdAndCartId_declaresModifyingDeleteQuery() throws NoSuchMethodException {
        var method = CartItemRepository.class.getMethod(
                "deleteCartItemByProductIdAndCartId",
                Long.class,
                Long.class
        );

        Query query = method.getAnnotation(Query.class);

        assertEquals(
                "DELETE FROM CartItem ci WHERE ci.cart.id = ?1 AND ci.product.id = ?2",
                query.value()
        );
        assertTrue(method.isAnnotationPresent(Modifying.class));
    }
}
