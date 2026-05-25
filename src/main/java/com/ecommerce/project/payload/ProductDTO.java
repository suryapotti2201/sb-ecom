package com.ecommerce.project.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;
    @NotBlank(message = "ProductName should not be null or blank")
    private String productName;
    private String image;
    @Min(value = 1, message = "Price should be positive")
    private Double price;
    private Double discount;
    private Double specialPrice;
    @Min(value = 1, message = "Quantity should be positive")
    private Long quantity;
    private char status;
}
