package com.ecommerce.project.repository;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductIdAndStatus(Long productId, char status);

    boolean existsByProductName(@NotBlank(message = "ProductName should not be null or blank") String productName);

    Page<Product> findAllByStatus(char active, Pageable pageDetails);

    Page<Product> findAllByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails);

    Page<Product> findAllByCategory(Category category, Pageable pageDetails);

    List<Product> findByCategory(Category category);
}
