package com.ecommerce.project.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    @NotNull(message = "Address ID is required")
    @Positive(message = "Address ID must be greater than zero")
    private Long addressId;

    @NotBlank(message = "Payment type is required")
    @Size(max = 50, message = "Payment type must not exceed {max} characters")
    private String paymentType;

    @Size(max = 255, message = "Payment gateway payment ID must not exceed {max} characters")
    private String pgPaymentId;

    private String pgStatus;
    private String pgResponseMessage;


    @Size(max = 100, message = "Payment gateway name must not exceed {max} characters")
    private String pgName;
}
