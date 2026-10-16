package com.ecommerce.project.controller;

import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.service.AddressService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressDTO> addAddress(@Valid @RequestBody AddressDTO addressDTO){
        AddressDTO address =addressService.addAddress(addressDTO);
        return new ResponseEntity<>(address, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AddressDTO>> getAddresses(){
        List<AddressDTO> addressDTOs = addressService.getAddresses();
        return new ResponseEntity<>(addressDTOs, HttpStatus.OK);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressDTO> getAddress(
            @PathVariable @Positive(message = "Address ID must be greater than zero") Long addressId){
        AddressDTO addressDTO = addressService.getAddress(addressId);
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<AddressDTO>> getAddressForLoggedInUser(){
        List<AddressDTO> addressDTO = addressService.getAddressForLoggedInUser();
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(
            @PathVariable @Positive(message = "Address ID must be greater than zero") Long addressId,
            @Valid @RequestBody AddressDTO addressDTO){
        AddressDTO updatedAddressDTO = addressService.updateAddress(addressId, addressDTO);
        return new ResponseEntity<>(updatedAddressDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deletingAddress(
            @PathVariable @Positive(message = "Address ID must be greater than zero") Long addressId){
        String status = addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
