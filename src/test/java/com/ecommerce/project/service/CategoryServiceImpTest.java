package com.ecommerce.project.service;

import com.ecommerce.project.constants.SystemConstants;
import com.ecommerce.project.exception.EcommerceException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.response.CategoryResponse;
import com.ecommerce.project.util.AuthUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImpTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ProductService productService;

    @Mock
    private AuthUtil authUtil;

    @InjectMocks
    private CategoryServiceImp categoryService;

    @Test
    void createCategory_createsAndReturnsCategoryWhenNameIsAvailable() {
        CategoryDTO input = mock(CategoryDTO.class);
        Category category = new Category();
        category.setCategoryName("Electronics");
        Category savedCategory = new Category();
        CategoryDTO result = mock(CategoryDTO.class);
        User user = mock(User.class);

        when(modelMapper.map(input, Category.class)).thenReturn(category);
        when(categoryRepository.existsByCategoryName("Electronics")).thenReturn(false);
        when(authUtil.loggedInUser()).thenReturn(user);
        when(user.getUserName()).thenReturn("admin");
        when(categoryRepository.save(category)).thenReturn(savedCategory);
        when(modelMapper.map(savedCategory, CategoryDTO.class)).thenReturn(result);

        CategoryDTO actual = categoryService.createCategory(input);

        assertEquals(result, actual);
        assertEquals(SystemConstants.ACTIVE, category.getStatus());
        assertEquals("admin", category.getCreatedBy());
        assertEquals("admin", category.getLastUpdatedBy());
        verify(categoryRepository).save(category);
        verify(modelMapper).map(savedCategory, CategoryDTO.class);
    }

    @Test
    void createCategory_throwsWhenCategoryNameAlreadyExists() {
        CategoryDTO input = mock(CategoryDTO.class);
        Category category = new Category();
        category.setCategoryName("Books");

        when(modelMapper.map(input, Category.class)).thenReturn(category);
        when(categoryRepository.existsByCategoryName("Books")).thenReturn(true);

        EcommerceException exception = assertThrows(
                EcommerceException.class,
                () -> categoryService.createCategory(input)
        );

        assertEquals("Category with the name Books already exists !!!", exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
        verifyNoInteractions(authUtil);
    }

    @Test
    void getAllCategories_returnsMappedPageForAscendingSort() {
        Category category = new Category();
        CategoryDTO dto = mock(CategoryDTO.class);
        Page<Category> page = new PageImpl<>(List.of(category), Pageable.ofSize(2), 3);

        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(dto);

        CategoryResponse response =
                categoryService.getAllCategories(0, 2, "categoryName", SystemConstants.ASC);

        assertEquals(List.of(dto), response.getContent());
        assertEquals(0, response.getPageNumber());
        assertEquals(2, response.getPageSize());
        assertEquals(3, response.getTotalElements());
        assertEquals(2, response.getTotalPages());
        assertEquals(false, response.isLastPage());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(categoryRepository).findAll(pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();
        assertEquals(Sort.Direction.ASC, pageable.getSort().getOrderFor("categoryName").getDirection());
    }

    @Test
    void getAllCategories_usesDescendingSortWhenOrderIsNotAscending() {
        Page<Category> page = new PageImpl<>(
                List.of(new Category()),
                Pageable.ofSize(1),
                1
        );

        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(any(Category.class), eq(CategoryDTO.class)))
                .thenReturn(mock(CategoryDTO.class));

        categoryService.getAllCategories(1, 1, "categoryName", "desc");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(categoryRepository).findAll(pageableCaptor.capture());
        assertEquals(
                Sort.Direction.DESC,
                pageableCaptor.getValue().getSort().getOrderFor("categoryName").getDirection()
        );
    }

    @Test
    void getAllCategories_throwsWhenNoCategoriesExist() {
        when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        EcommerceException exception = assertThrows(
                EcommerceException.class,
                () -> categoryService.getAllCategories(0, 10, "categoryName", SystemConstants.ASC)
        );

        assertEquals("Categories are not yet created!!!", exception.getMessage());
        verifyNoInteractions(modelMapper);
    }

    @Test
    void deleteCategory_marksCategoryInactiveAndDeletesItsProducts() {
        Long categoryId = 10L;
        Category category = new Category();
        Product firstProduct = new Product();
        firstProduct.setProductId(1L);
        Product secondProduct = new Product();
        secondProduct.setProductId(2L);
        User user = mock(User.class);
        CategoryDTO result = mock(CategoryDTO.class);

        when(categoryRepository.findByCategoryIdAndStatus(categoryId, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(category));
        when(authUtil.loggedInUser()).thenReturn(user);
        when(user.getUserName()).thenReturn("admin");
        when(categoryRepository.save(category)).thenReturn(category);
        when(productRepository.findByCategory(category))
                .thenReturn(List.of(firstProduct, secondProduct));
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(result);

        CategoryDTO actual = categoryService.deleteCategory(categoryId);

        assertEquals(result, actual);
        assertEquals(SystemConstants.INACTIVE, category.getStatus());
        assertEquals("admin", category.getLastUpdatedBy());
        verify(categoryRepository).save(category);
        verify(productService).deleteProduct(1L);
        verify(productService).deleteProduct(2L);
    }

    @Test
    void deleteCategory_throwsWhenActiveCategoryDoesNotExist() {
        Long categoryId = 10L;
        when(categoryRepository.findByCategoryIdAndStatus(categoryId, SystemConstants.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(categoryId)
        );

        verify(categoryRepository, never()).save(any(Category.class));
        verifyNoInteractions(productRepository, productService, authUtil);
    }

    @Test
    void updateCategory_updatesExistingCategoryWhenNameIsAvailable() {
        Long categoryId = 5L;
        CategoryDTO input = mock(CategoryDTO.class);
        Category mappedCategory = new Category();
        mappedCategory.setCategoryName("Home");
        Category updatedCategory = new Category();
        CategoryDTO result = mock(CategoryDTO.class);
        User user = mock(User.class);

        when(modelMapper.map(input, Category.class)).thenReturn(mappedCategory);
        when(categoryRepository.findByCategoryIdAndStatus(categoryId, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(new Category()));
        when(categoryRepository.existsByCategoryName("Home")).thenReturn(false);
        when(authUtil.loggedInUser()).thenReturn(user);
        when(user.getUserName()).thenReturn("admin");
        when(categoryRepository.save(mappedCategory)).thenReturn(updatedCategory);
        when(modelMapper.map(updatedCategory, CategoryDTO.class)).thenReturn(result);

        CategoryDTO actual = categoryService.updateCategory(categoryId, input);

        assertEquals(result, actual);
        assertEquals(categoryId, mappedCategory.getCategoryId());
        assertEquals(SystemConstants.ACTIVE, mappedCategory.getStatus());
        assertEquals("admin", mappedCategory.getLastUpdatedBy());
        verify(categoryRepository).save(mappedCategory);
    }

    @Test
    void updateCategory_throwsWhenNameAlreadyExists() {
        Long categoryId = 5L;
        CategoryDTO input = mock(CategoryDTO.class);
        Category mappedCategory = new Category();
        mappedCategory.setCategoryName("Home");

        when(modelMapper.map(input, Category.class)).thenReturn(mappedCategory);
        when(categoryRepository.findByCategoryIdAndStatus(categoryId, SystemConstants.ACTIVE))
                .thenReturn(Optional.of(new Category()));
        when(categoryRepository.existsByCategoryName("Home")).thenReturn(true);

        assertThrows(
                EcommerceException.class,
                () -> categoryService.updateCategory(categoryId, input)
        );

        verify(categoryRepository, never()).save(any(Category.class));
        verifyNoInteractions(authUtil);
    }

    @Test
    void updateCategory_throwsWhenCategoryDoesNotExist() {
        Long categoryId = 5L;
        CategoryDTO input = mock(CategoryDTO.class);

        when(modelMapper.map(input, Category.class)).thenReturn(new Category());
        when(categoryRepository.findByCategoryIdAndStatus(categoryId, SystemConstants.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.updateCategory(categoryId, input)
        );

        verify(categoryRepository, never()).existsByCategoryName(anyString());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getCategoryById_returnsMappedCategoryWhenPresent() {
        Long categoryId = 7L;
        Category category = new Category();
        CategoryDTO result = mock(CategoryDTO.class);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(result);

        assertEquals(result, categoryService.getCategoryById(categoryId));

        verify(categoryRepository).findById(categoryId);
        verify(modelMapper).map(category, CategoryDTO.class);
    }

    @Test
    void getCategoryById_throwsWhenCategoryDoesNotExist() {
        Long categoryId = 7L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.getCategoryById(categoryId)
        );

        verifyNoInteractions(modelMapper);
    }

    @Test
    void getActiveCategories_returnsOnlyMappedActiveCategories() {
        Category category = new Category();
        CategoryDTO dto = mock(CategoryDTO.class);
        Page<Category> page = new PageImpl<>(List.of(category), Pageable.ofSize(5), 1);

        when(categoryRepository.findAllByStatus(eq(SystemConstants.ACTIVE), any(Pageable.class)))
                .thenReturn(page);
        when(modelMapper.map(category, CategoryDTO.class)).thenReturn(dto);

        CategoryResponse response =
                categoryService.getActiveCategories(0, 5, "categoryName", SystemConstants.ASC);

        assertEquals(List.of(dto), response.getContent());
        assertEquals(0, response.getPageNumber());
        assertEquals(5, response.getPageSize());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertEquals(true, response.isLastPage());

        verify(categoryRepository)
                .findAllByStatus(eq(SystemConstants.ACTIVE), any(Pageable.class));
    }

    @Test
    void getActiveCategories_throwsWhenNoActiveCategoriesExist() {
        when(categoryRepository.findAllByStatus(eq(SystemConstants.ACTIVE), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        EcommerceException exception = assertThrows(
                EcommerceException.class,
                () -> categoryService.getActiveCategories(0, 10, "categoryName", SystemConstants.ASC)
        );

        assertEquals("Categories are not yet created!!!", exception.getMessage());
        verifyNoInteractions(modelMapper);
    }
}
