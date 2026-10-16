package com.ecommerce.project.controller;

import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.response.ProductResponse;
import com.ecommerce.project.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void createProductDelegatesToServiceAndReturnsCreatedResponse() {
        ProductDTO productDto = org.mockito.Mockito.mock(ProductDTO.class);
        ProductDTO createdProduct = org.mockito.Mockito.mock(ProductDTO.class);
        Long categoryId = 10L;

        when(productService.createProduct(productDto, categoryId)).thenReturn(createdProduct);

        ResponseEntity<ProductDTO> response =
                productController.createProduct(productDto, categoryId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(createdProduct, response.getBody());
        verify(productService).createProduct(productDto, categoryId);
    }

    @Test
    void getAllProductsDelegatesPaginationAndSortingArguments() {
        ProductResponse productResponse = org.mockito.Mockito.mock(ProductResponse.class);
        Integer pageNumber = 2;
        Integer pageSize = 20;
        String sortBy = "price";
        String sortOrder = "desc";

        when(productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder))
                .thenReturn(productResponse);

        ResponseEntity<ProductResponse> response =
                productController.getAllProducts(pageNumber, pageSize, sortBy, sortOrder);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(productResponse, response.getBody());
        verify(productService).getAllProducts(pageNumber, pageSize, sortBy, sortOrder);
    }

    @Test
    void deleteProductDelegatesToServiceAndReturnsOkResponse() {
        Long productId = 25L;
        ProductDTO deletedProduct = org.mockito.Mockito.mock(ProductDTO.class);

        when(productService.deleteProduct(productId)).thenReturn(deletedProduct);

        ResponseEntity<ProductDTO> response = productController.deleteProduct(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(deletedProduct, response.getBody());
        verify(productService).deleteProduct(productId);
    }

    @Test
    void updateProductDelegatesProductAndIdAndReturnsOkResponse() {
        Long productId = 30L;
        ProductDTO productDto = org.mockito.Mockito.mock(ProductDTO.class);
        ProductDTO updatedProduct = org.mockito.Mockito.mock(ProductDTO.class);

        when(productService.updateProduct(productDto, productId)).thenReturn(updatedProduct);

        ResponseEntity<ProductDTO> response =
                productController.updateProduct(productId, productDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updatedProduct, response.getBody());
        verify(productService).updateProduct(productDto, productId);
    }

    @Test
    void getProductByIdDelegatesToServiceAndReturnsOkResponse() {
        Long productId = 42L;
        ProductDTO product = org.mockito.Mockito.mock(ProductDTO.class);

        when(productService.getProductById(productId)).thenReturn(product);

        ResponseEntity<ProductDTO> response = productController.getProductById(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(product, response.getBody());
        verify(productService).getProductById(productId);
    }

    @Test
    void getAllActiveProductsDelegatesPaginationAndSortingArguments() {
        ProductResponse productResponse = org.mockito.Mockito.mock(ProductResponse.class);
        Integer pageNumber = 1;
        Integer pageSize = 10;
        String sortBy = "productName";
        String sortOrder = "asc";

        when(productService.getAllActiveProducts(pageNumber, pageSize, sortBy, sortOrder))
                .thenReturn(productResponse);

        ResponseEntity<ProductResponse> response =
                productController.getAllActiveProducts(pageNumber, pageSize, sortBy, sortOrder);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(productResponse, response.getBody());
        verify(productService).getAllActiveProducts(pageNumber, pageSize, sortBy, sortOrder);
    }

    @Test
    void getAllProductsByCategoryDelegatesCategoryAndQueryArguments() {
        Long categoryId = 7L;
        Integer pageNumber = 3;
        Integer pageSize = 15;
        String sortBy = "rating";
        String sortOrder = "desc";
        ProductResponse productResponse = org.mockito.Mockito.mock(ProductResponse.class);

        when(productService.getAllProductsByCategory(
                categoryId, pageNumber, pageSize, sortBy, sortOrder))
                .thenReturn(productResponse);

        ResponseEntity<ProductResponse> response =
                productController.getAllProductsByCategory(
                        categoryId, pageNumber, pageSize, sortBy, sortOrder);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(productResponse, response.getBody());
        verify(productService).getAllProductsByCategory(
                categoryId, pageNumber, pageSize, sortBy, sortOrder);
    }

    @Test
    void getAllProductsByKeywordDelegatesKeywordAndQueryArguments() {
        String keyword = "laptop";
        Integer pageNumber = 1;
        Integer pageSize = 25;
        String sortBy = "price";
        String sortOrder = "asc";
        ProductResponse productResponse = org.mockito.Mockito.mock(ProductResponse.class);

        when(productService.searchProductByKeyword(
                keyword, pageNumber, pageSize, sortBy, sortOrder))
                .thenReturn(productResponse);

        ResponseEntity<ProductResponse> response =
                productController.getAllProductsByKeyword(
                        keyword, pageNumber, pageSize, sortBy, sortOrder);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(productResponse, response.getBody());
        verify(productService).searchProductByKeyword(
                keyword, pageNumber, pageSize, sortBy, sortOrder);
    }
}
