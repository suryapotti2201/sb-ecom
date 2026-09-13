package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;
    private String street;
    private String buildingName;
    private String city;
    private String country;
    private String pincode;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdOn;
    private String createdBy;
    private LocalDateTime updatedOn;
    private String updatedBy;

    public Address(String street, String buildingName, String city, String country, String pincode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.country = country;
        this.pincode = pincode;
    }
}
