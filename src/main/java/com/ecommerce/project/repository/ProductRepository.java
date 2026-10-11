package com.ecommerce.project.repository;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductIdAndStatus(Long productId, char status);

    boolean existsByProductName(@NotBlank(message = "ProductName should not be null or blank") String productName);

    Page<Product> findAllByStatus(char active, Pageable pageDetails);

    Page<Product> findAllByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails);

    Page<Product> findAllByCategory(Category category, Pageable pageDetails);

    List<Product> findByCategory(Category category);

    @Modifying(flushAutomatically = true)
    @Query("""
        UPDATE Product p
        SET p.quantity = p.quantity - :quantity,
            p.lastUpdatedBy = :updatedBy,
            p.lastUpdatedOn = :updatedOn
        WHERE p.productId = :productId
          AND p.status = :activeStatus
          AND :quantity > 0
          AND p.quantity >= :quantity
        """)
    int decreaseStockIfAvailable(@Param("productId") Long productId, @Param("quantity") Long quantity,
                                 @Param("activeStatus") char activeStatus, @Param("updatedBy") String updatedBy,
                                 @Param("updatedOn") LocalDateTime updatedOn);
}
