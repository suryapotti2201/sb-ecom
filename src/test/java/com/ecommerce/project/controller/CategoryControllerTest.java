package com.ecommerce.project.controller;

import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.response.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    void createCategoryReturnsCreatedCategoryAndCreatedStatus() {
        CategoryDTO categoryDto = new CategoryDTO();
        CategoryDTO createdCategory = new CategoryDTO();

        when(categoryService.createCategory(categoryDto)).thenReturn(createdCategory);

        ResponseEntity<CategoryDTO> response = categoryController.createCategory(categoryDto);

        assertSame(createdCategory, response.getBody());
        assertSame(HttpStatus.CREATED, response.getStatusCode());
        verify(categoryService).createCategory(categoryDto);
    }

    @Test
    void createCategoryPropagatesServiceException() {
        CategoryDTO categoryDto = new CategoryDTO();
        RuntimeException exception = new RuntimeException("creation failed");

        when(categoryService.createCategory(categoryDto)).thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> categoryController.createCategory(categoryDto)
        );

        assertSame(exception, thrown);
        verify(categoryService).createCategory(categoryDto);
    }

    @Test
    void getAllCategoriesReturnsCategoryResponseAndOkStatus() {
        Integer pageNumber = 2;
        Integer pageSize = 10;
        String sortBy = "name";
        String sortOrder = "asc";
        CategoryResponse categoryResponse = new CategoryResponse();

        when(categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder))
                .thenReturn(categoryResponse);

        ResponseEntity<CategoryResponse> response = categoryController.getAllCategories(
                pageNumber, pageSize, sortBy, sortOrder
        );

        assertSame(categoryResponse, response.getBody());
        assertSame(HttpStatus.OK, response.getStatusCode());
        verify(categoryService).getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
    }

    @Test
    void deleteCategoryReturnsDeletedCategoryAndOkStatus() {
        Long categoryId = 7L;
        CategoryDTO deletedCategory = new CategoryDTO();

        when(categoryService.deleteCategory(categoryId)).thenReturn(deletedCategory);

        ResponseEntity<CategoryDTO> response = categoryController.deleteCategory(categoryId);

        assertSame(deletedCategory, response.getBody());
        assertSame(HttpStatus.OK, response.getStatusCode());
        verify(categoryService).deleteCategory(categoryId);
    }

    @Test
    void updateCategoryReturnsUpdatedCategoryAndOkStatus() {
        Long categoryId = 9L;
        CategoryDTO categoryDto = new CategoryDTO();
        CategoryDTO updatedCategory = new CategoryDTO();

        when(categoryService.updateCategory(categoryId, categoryDto)).thenReturn(updatedCategory);

        ResponseEntity<CategoryDTO> response =
                categoryController.updateCategory(categoryId, categoryDto);

        assertSame(updatedCategory, response.getBody());
        assertSame(HttpStatus.OK, response.getStatusCode());
        verify(categoryService).updateCategory(categoryId, categoryDto);
    }

    @Test
    void getCategoryByIdReturnsCategoryAndOkStatus() {
        Long categoryId = 11L;
        CategoryDTO category = new CategoryDTO();

        when(categoryService.getCategoryById(categoryId)).thenReturn(category);

        ResponseEntity<CategoryDTO> response = categoryController.getCategoryById(categoryId);

        assertSame(category, response.getBody());
        assertSame(HttpStatus.OK, response.getStatusCode());
        verify(categoryService).getCategoryById(categoryId);
    }

    @Test
    void getActiveCategoriesReturnsCategoryResponseAndOkStatus() {
        Integer pageNumber = 1;
        Integer pageSize = 20;
        String sortBy = "categoryName";
        String sortOrder = "desc";
        CategoryResponse categoryResponse = new CategoryResponse();

        when(categoryService.getActiveCategories(pageNumber, pageSize, sortBy, sortOrder))
                .thenReturn(categoryResponse);

        ResponseEntity<CategoryResponse> response = categoryController.getActiveCategories(
                pageNumber, pageSize, sortBy, sortOrder
        );

        assertSame(categoryResponse, response.getBody());
        assertSame(HttpStatus.OK, response.getStatusCode());
        verify(categoryService).getActiveCategories(pageNumber, pageSize, sortBy, sortOrder);
    }
}
