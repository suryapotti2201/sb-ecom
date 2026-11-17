package com.ecommerce.project.repository;

import com.ecommerce.project.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryIdAndStatus(Long categoryId, char active);

    boolean existsByCategoryName(@NotBlank(message = "CategoryName should not be null or blank") String categoryName);

    Page<Category> findAllByStatus(char active, Pageable pageDetails);

    boolean existsByCategoryNameAndCategoryIdNot(@NotBlank(message = "Category name is required") @Size(min = 2, max = 100, message = "Category name must be between {min} and {max} characters") String categoryName, Long categoryId);
}
