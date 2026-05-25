package com.ecommerce.project.controller;

import com.ecommerce.project.constants.SystemConstants;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/app/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDto, @PathVariable Long categoryId){
        return new ResponseEntity<>(productService.createProduct(productDto, categoryId), HttpStatus.CREATED);
    }

    @GetMapping("/app/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(value = "pageNumber", defaultValue = SystemConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = SystemConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = SystemConstants.SORT_PRODUCTS_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = SystemConstants.SORT_DIR) String sortOrder){
        return new ResponseEntity<>(productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    @DeleteMapping("/app/admin/product/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){
        return new ResponseEntity<>(productService.deleteProduct(productId), HttpStatus.OK);
    }

    @PutMapping("/app/admin/product/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductDTO productDto){
        return new ResponseEntity<>(productService.updateProduct(productDto, productId), HttpStatus.OK);
    }

    @GetMapping("/app/public/product/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long productId){
        return new ResponseEntity<>(productService.getProductById(productId), HttpStatus.OK);

    }

    @GetMapping("/app/public/products/active")
    public ResponseEntity<ProductResponse> getAllActiveProducts(
            @RequestParam(value = "pageNumber", defaultValue = SystemConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = SystemConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = SystemConstants.SORT_PRODUCTS_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = SystemConstants.SORT_DIR) String sortOrder){
        return new ResponseEntity<>(productService.getAllActiveProducts(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    @GetMapping("/app/public/category/{categoryId}/products")
    public ResponseEntity<ProductResponse> getAllProductsByCategory(@PathVariable Long categoryId,
            @RequestParam(value = "pageNumber", defaultValue = SystemConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = SystemConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = SystemConstants.SORT_PRODUCTS_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = SystemConstants.SORT_DIR) String sortOrder){
        return new ResponseEntity<>(productService.getAllProductsByCategory(categoryId, pageNumber, pageSize, sortBy,
                sortOrder), HttpStatus.OK);
    }

    @GetMapping("/app/public/product/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getAllProductsByKeyword(@PathVariable String keyword,
            @RequestParam(value = "pageNumber", defaultValue = SystemConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = SystemConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = SystemConstants.SORT_PRODUCTS_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = SystemConstants.SORT_DIR) String sortOrder){
        return new ResponseEntity<>(productService.searchProductByKeyword(keyword, pageNumber, pageSize, sortBy,
                sortOrder), HttpStatus.OK);
    }
}