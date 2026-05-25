package com.ecommerce.project.repository;

import com.ecommerce.project.model.Category;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryIdAndStatus(Long categoryId, char active);

    boolean existsByCategoryName(@NotBlank(message = "CategoryName should not be null or blank") String categoryName);

    Page<Category> findAllByStatus(char active, Pageable pageDetails);
}
