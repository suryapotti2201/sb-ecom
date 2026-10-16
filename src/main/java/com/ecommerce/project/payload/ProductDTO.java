package com.ecommerce.project.payload;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    @NotBlank(message = "ProductName should not be null or blank")
    @Size(min = 2, max = 150, message = "Product name must be between 2 and 150 characters")
    private String productName;

    @Size(max = 255, message = "Image filename must not exceed {max} characters")
    private String image;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0", inclusive = false, message = "Price must be greater than zero")
    @Digits(integer = 10, fraction = 2,
            message = "Price must have at most {integer} integer digits and {fraction} decimal places")
    private BigDecimal price;

    @NotNull(message = "Discount is required")
    @DecimalMin(value = "0", message = "Discount must not be negative")
    @DecimalMax(value = "100", message = "Discount must not exceed {value} percent")
    @Digits(integer = 3, fraction = 2,
            message = "Discount must have at most {integer} integer digits and {fraction} decimal places")
    private BigDecimal discount;

    private BigDecimal specialPrice;

    @NotNull(message = "Stock quantity is required")
    @PositiveOrZero(message = "Stock quantity must be zero or greater")
    private Long quantity;

    private char status;
}
