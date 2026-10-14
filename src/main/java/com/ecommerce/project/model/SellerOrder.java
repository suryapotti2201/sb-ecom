package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "seller_order",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"order_id", "seller_id"},
                name = "unique_order_seller")
    })
@NoArgsConstructor
@AllArgsConstructor
public class SellerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long sellerOrderId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    private String remark;

    private String createdBy;
    private LocalDateTime createdOn;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedOn;
}
