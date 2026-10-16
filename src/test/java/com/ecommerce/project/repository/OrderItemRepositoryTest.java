package com.ecommerce.project.repository;

import com.ecommerce.project.model.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderItemRepositoryTest {

    @Test
    void shouldBeDeclaredAsSpringDataRepository() {
        assertTrue(OrderItemRepository.class.isAnnotationPresent(Repository.class));
        assertTrue(JpaRepository.class.isAssignableFrom(OrderItemRepository.class));
    }

    @Test
    void shouldExtendJpaRepositoryForOrderItemAndLongIdentifier() {
        Type[] genericInterfaces = OrderItemRepository.class.getGenericInterfaces();

        assertTrue(genericInterfaces.length > 0);
        assertTrue(genericInterfaces[0] instanceof ParameterizedType);

        ParameterizedType repositoryType = (ParameterizedType) genericInterfaces[0];

        assertSame(JpaRepository.class, repositoryType.getRawType());
        assertArrayEquals(
                new Type[]{OrderItem.class, Long.class},
                repositoryType.getActualTypeArguments()
        );
    }
}
