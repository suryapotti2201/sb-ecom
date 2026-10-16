package com.ecommerce.project.repository;

import com.ecommerce.project.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryTest {

    @Mock
    private OrderRepository orderRepository;

    @Test
    void shouldBeJpaRepositoryForOrderEntitiesWithLongIdentifiers() {
        assertTrue(JpaRepository.class.isAssignableFrom(OrderRepository.class));

        ParameterizedType repositoryType =
                (ParameterizedType) OrderRepository.class.getGenericInterfaces()[0];

        assertEquals(JpaRepository.class, repositoryType.getRawType());
        assertEquals(Order.class, repositoryType.getActualTypeArguments()[0]);
        assertEquals(Long.class, repositoryType.getActualTypeArguments()[1]);
    }

    @Test
    void saveShouldReturnPersistedOrder() {
        Order order = org.mockito.Mockito.mock(Order.class);

        when(orderRepository.save(order)).thenReturn(order);

        Order persistedOrder = orderRepository.save(order);

        assertSame(order, persistedOrder);
        verify(orderRepository).save(order);
    }

    @Test
    void findByIdShouldReturnExistingOrder() {
        Long orderId = 1L;
        Order order = org.mockito.Mockito.mock(Order.class);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Optional<Order> result = orderRepository.findById(orderId);

        assertTrue(result.isPresent());
        assertSame(order, result.orElseThrow());
        verify(orderRepository).findById(orderId);
    }

    @Test
    void findByIdShouldReturnEmptyWhenOrderDoesNotExist() {
        Long orderId = 999L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Optional<Order> result = orderRepository.findById(orderId);

        assertFalse(result.isPresent());
        verify(orderRepository).findById(orderId);
    }

    @Test
    void findAllShouldReturnAllOrders() {
        Order firstOrder = org.mockito.Mockito.mock(Order.class);
        Order secondOrder = org.mockito.Mockito.mock(Order.class);
        List<Order> orders = List.of(firstOrder, secondOrder);

        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderRepository.findAll();

        assertEquals(orders, result);
        verify(orderRepository).findAll();
    }

    @Test
    void existsByIdShouldReturnTrueWhenOrderExists() {
        Long orderId = 1L;

        when(orderRepository.existsById(orderId)).thenReturn(true);

        assertTrue(orderRepository.existsById(orderId));

        verify(orderRepository).existsById(orderId);
    }

    @Test
    void countShouldReturnNumberOfOrders() {
        when(orderRepository.count()).thenReturn(3L);

        assertEquals(3L, orderRepository.count());

        verify(orderRepository, times(1)).count();
    }

    @Test
    void deleteByIdShouldDeleteTheRequestedOrder() {
        Long orderId = 1L;

        orderRepository.deleteById(orderId);

        verify(orderRepository).deleteById(orderId);
    }

    @Test
    void deleteByIdShouldPropagateRepositoryException() {
        Long orderId = 1L;
        RuntimeException exception = new RuntimeException("Unable to delete order");

        doThrow(exception).when(orderRepository).deleteById(orderId);

        RuntimeException thrown = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> orderRepository.deleteById(orderId)
        );

        assertSame(exception, thrown);
        verify(orderRepository).deleteById(orderId);
    }
}
