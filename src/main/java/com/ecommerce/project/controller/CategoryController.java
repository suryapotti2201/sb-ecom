package com.ecommerce.project.controller;

import com.ecommerce.project.constants.SystemConstants;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.response.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/app/admin/category")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDto){
        return new ResponseEntity<>(categoryService.createCategory(categoryDto), HttpStatus.CREATED);
    }
    
    @GetMapping("/app/public/category")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(value = "pageNumber", defaultValue = SystemConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = SystemConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = SystemConstants.SORT_CATEGORY_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = SystemConstants.SORT_DIR) String sortOrder){
        return new ResponseEntity<>(categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    @DeleteMapping("/app/admin/category/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryDTO> deleteCategory(
            @PathVariable @Positive(message = "Category ID must be greater than zero") Long categoryId){
        return new ResponseEntity<>(categoryService.deleteCategory(categoryId), HttpStatus.OK);
    }

    @PutMapping("/app/admin/category/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryDTO>  updateCategory(
            @PathVariable @Positive(message = "Category ID must be greater than zero") Long categoryId,
            @Valid @RequestBody CategoryDTO categoryDto){
        return new ResponseEntity<>(categoryService.updateCategory(categoryId, categoryDto), HttpStatus.OK);
    }

    @GetMapping("/app/public/category/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryById(
            @PathVariable @Positive(message = "Category ID must be greater than zero") Long categoryId){
        return new ResponseEntity<>(categoryService.getCategoryById(categoryId), HttpStatus.OK);
    }

    @GetMapping("/app/public/category/active")
    public ResponseEntity<CategoryResponse> getActiveCategories(
            @RequestParam(value = "pageNumber", defaultValue = SystemConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = SystemConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = SystemConstants.SORT_CATEGORY_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = SystemConstants.SORT_DIR) String sortOrder){
        return new ResponseEntity<>(categoryService.getActiveCategories(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }
}
