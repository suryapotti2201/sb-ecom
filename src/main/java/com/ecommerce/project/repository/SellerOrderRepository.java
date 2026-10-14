package com.ecommerce.project.repository;

import com.ecommerce.project.model.OrderStatus;
import com.ecommerce.project.model.SellerOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerOrderRepository extends JpaRepository<SellerOrder, Long> {

    @Query("SELECT so FROM SellerOrder so where so.sellerOrderId = ?1 and so.seller.userId = ?2")
    Optional<SellerOrder> findBySellerOrderIdAndSellerId(Long sellerOrderId, Long sellerId);

    @Query("SELECT so FROM SellerOrder so where so.seller.userId = ?1")
    Page<SellerOrder> findAllByUserId(Long sellerId, Pageable pageDetails);

    @Query("SELECT so FROM SellerOrder so where so.seller.userId = ?1 and so.orderStatus = ?2")
    Page<SellerOrder> findAllByUserIdAndStatus(Long sellerId, OrderStatus status, Pageable pageDetails);
}
