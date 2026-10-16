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
public class AddressDTO {
    @NotBlank(message = "Street is required")
    @Size(max = 255, message = "Street must not exceed {max} characters")
    private String street;

    @Size(max = 150, message = "Building name must not exceed {max} characters")
    private String buildingName;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed {max} characters")
    private String city;


    @NotBlank(message = "Country is required")
    @Size(max = 60, message = "Country must not exceed {max} characters")
    private String country;

    @NotBlank(message = "PIN code is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$",
            message = "PIN code must contain exactly 6 digits and must not start with zero")
    private String pincode;
}
