package com.ecommerce.project.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerOrderDTO {
    @NotBlank(message = "Order status is required")
    @Pattern(regexp = "^(PACKED|IN_TRANSIT|OUT_FOR_DELIVERY|DELIVERED)$",
            message = "Order status must be PACKED, IN_TRANSIT, OUT_FOR_DELIVERY, or DELIVERED"
    )
    private String orderStatus;

    @Size(max = 500, message = "Remark must not exceed {max} characters")
    private String remark;
}
