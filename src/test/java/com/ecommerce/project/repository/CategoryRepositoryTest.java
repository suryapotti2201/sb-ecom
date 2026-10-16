package com.ecommerce.project.repository;

import com.ecommerce.project.model.Category;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryRepositoryTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Category category;

    @Test
    void findByCategoryIdAndStatusReturnsCategoryWhenMatchingCategoryExists() {
        Long categoryId = 1L;
        char active = 'Y';
        Optional<Category> expected = Optional.of(category);

        when(categoryRepository.findByCategoryIdAndStatus(categoryId, active))
                .thenReturn(expected);

        Optional<Category> result =
                categoryRepository.findByCategoryIdAndStatus(categoryId, active);

        assertSame(expected, result);
        assertTrue(result.isPresent());
        assertSame(category, result.get());
        verify(categoryRepository).findByCategoryIdAndStatus(categoryId, active);
    }

    @Test
    void findByCategoryIdAndStatusReturnsEmptyWhenNoMatchingCategoryExists() {
        Long categoryId = 99L;
        char active = 'Y';

        when(categoryRepository.findByCategoryIdAndStatus(categoryId, active))
                .thenReturn(Optional.empty());

        Optional<Category> result =
                categoryRepository.findByCategoryIdAndStatus(categoryId, active);

        assertTrue(result.isEmpty());
        verify(categoryRepository).findByCategoryIdAndStatus(categoryId, active);
    }

    @Test
    void existsByCategoryNameReturnsTrueWhenCategoryNameExists() {
        String categoryName = "Electronics";

        when(categoryRepository.existsByCategoryName(categoryName)).thenReturn(true);

        boolean result = categoryRepository.existsByCategoryName(categoryName);

        assertTrue(result);
        verify(categoryRepository).existsByCategoryName(categoryName);
    }

    @Test
    void existsByCategoryNameReturnsFalseWhenCategoryNameDoesNotExist() {
        String categoryName = "Unknown";

        when(categoryRepository.existsByCategoryName(categoryName)).thenReturn(false);

        boolean result = categoryRepository.existsByCategoryName(categoryName);

        assertFalse(result);
        verify(categoryRepository).existsByCategoryName(categoryName);
    }

    @Test
    void findAllByStatusReturnsMatchingCategoriesPage() {
        char active = 'Y';
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> expected = new PageImpl<>(List.of(category), pageable, 1);

        when(categoryRepository.findAllByStatus(active, pageable)).thenReturn(expected);

        Page<Category> result = categoryRepository.findAllByStatus(active, pageable);

        assertSame(expected, result);
        assertSame(category, result.getContent().get(0));
        verify(categoryRepository).findAllByStatus(active, pageable);
    }

    @Test
    void findAllByStatusReturnsEmptyPageWhenNoCategoriesMatch() {
        char active = 'N';
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> expected = Page.empty(pageable);

        when(categoryRepository.findAllByStatus(active, pageable)).thenReturn(expected);

        Page<Category> result = categoryRepository.findAllByStatus(active, pageable);

        assertSame(expected, result);
        assertTrue(result.isEmpty());
        verify(categoryRepository).findAllByStatus(active, pageable);
    }
}
