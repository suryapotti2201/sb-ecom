package com.ecommerce.project.repository;

import com.ecommerce.project.model.OrderStatus;
import com.ecommerce.project.model.SellerOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerOrderRepositoryTest {

    @Mock
    private SellerOrderRepository repository;

    @Mock
    private SellerOrder sellerOrder;

    @Test
    void findBySellerOrderIdAndSellerIdReturnsMatchingSellerOrder() {
        Long sellerOrderId = 10L;
        Long sellerId = 20L;
        Optional<SellerOrder> expected = Optional.of(sellerOrder);

        when(repository.findBySellerOrderIdAndSellerId(sellerOrderId, sellerId))
                .thenReturn(expected);

        Optional<SellerOrder> result =
                repository.findBySellerOrderIdAndSellerId(sellerOrderId, sellerId);

        assertSame(expected, result);
        verify(repository).findBySellerOrderIdAndSellerId(sellerOrderId, sellerId);
    }

    @Test
    void findBySellerOrderIdAndSellerIdReturnsEmptyWhenNoOrderMatches() {
        Long sellerOrderId = 10L;
        Long sellerId = 20L;

        when(repository.findBySellerOrderIdAndSellerId(sellerOrderId, sellerId))
                .thenReturn(Optional.empty());

        Optional<SellerOrder> result =
                repository.findBySellerOrderIdAndSellerId(sellerOrderId, sellerId);

        assertTrue(result.isEmpty());
        verify(repository).findBySellerOrderIdAndSellerId(sellerOrderId, sellerId);
    }

    @Test
    void findAllByUserIdReturnsSellerOrdersForRequestedPage() {
        Long sellerId = 20L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<SellerOrder> expected =
                new PageImpl<>(List.of(sellerOrder), pageable, 1);

        when(repository.findAllByUserId(sellerId, pageable)).thenReturn(expected);

        Page<SellerOrder> result = repository.findAllByUserId(sellerId, pageable);

        assertSame(expected, result);
        verify(repository).findAllByUserId(sellerId, pageable);
    }

    @Test
    void findAllByUserIdReturnsEmptyPageWhenSellerHasNoOrders() {
        Long sellerId = 20L;
        Pageable pageable = PageRequest.of(1, 10);
        Page<SellerOrder> expected = new PageImpl<>(List.of(), pageable, 0);

        when(repository.findAllByUserId(sellerId, pageable)).thenReturn(expected);

        Page<SellerOrder> result = repository.findAllByUserId(sellerId, pageable);

        assertTrue(result.isEmpty());
        verify(repository).findAllByUserId(sellerId, pageable);
    }

    @Test
    void findAllByUserIdAndStatusReturnsOrdersForRequestedStatusAndPage() {
        Long sellerId = 20L;
        Pageable pageable = PageRequest.of(0, 10);
        OrderStatus status = null;
        Page<SellerOrder> expected =
                new PageImpl<>(List.of(sellerOrder), pageable, 1);

        when(repository.findAllByUserIdAndStatus(sellerId, status, pageable))
                .thenReturn(expected);

        Page<SellerOrder> result =
                repository.findAllByUserIdAndStatus(sellerId, status, pageable);

        assertSame(expected, result);
        verify(repository).findAllByUserIdAndStatus(sellerId, status, pageable);
    }

    @Test
    void findAllByUserIdAndStatusReturnsEmptyPageWhenNoOrdersMatch() {
        Long sellerId = 20L;
        Pageable pageable = PageRequest.of(0, 10);
        OrderStatus status = null;
        Page<SellerOrder> expected = new PageImpl<>(List.of(), pageable, 0);

        when(repository.findAllByUserIdAndStatus(sellerId, status, pageable))
                .thenReturn(expected);

        Page<SellerOrder> result =
                repository.findAllByUserIdAndStatus(sellerId, status, pageable);

        assertTrue(result.isEmpty());
        verify(repository).findAllByUserIdAndStatus(sellerId, status, pageable);
    }
}
