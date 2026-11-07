package com.ecommerce.project.controller;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AddressController {

    @Autowired
    AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressDTO> addAddress(@Valid @RequestBody AddressDTO addressDTO){
        log.info("AddressController => addAddress : In");
        AddressDTO address =addressService.addAddress(addressDTO);
        log.info("AddressController => addAddress : Out");
        return new ResponseEntity<>(address, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AddressDTO>> getAddresses(){
        log.info("AddressController => getAddresses : In");
        List<AddressDTO> addressDTOs = addressService.getAddresses();
        log.info("AddressController => getAddresses : Out");
        return new ResponseEntity<>(addressDTOs, HttpStatus.OK);
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressDTO> getAddress(
            @PathVariable @Positive(message = "Address ID must be greater than zero") Long addressId){
        log.info("AddressController => getAddress : In");
        AddressDTO addressDTO = addressService.getAddress(addressId);
        log.info("AddressController => getAddress : Out");
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<AddressDTO>> getAddressForLoggedInUser(){
        log.info("AddressController => getAddressForLoggedInUser : In");
        List<AddressDTO> addressDTO = addressService.getAddressForLoggedInUser();
        log.info("AddressController => getAddressForLoggedInUser : Out");
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(
            @PathVariable @Positive(message = "Address ID must be greater than zero") Long addressId,
            @Valid @RequestBody AddressDTO addressDTO){
        log.info("AddressController => updateAddress : In");
        AddressDTO updatedAddressDTO = addressService.updateAddress(addressId, addressDTO);
        log.info("AddressController => updateAddress : Out");
        return new ResponseEntity<>(updatedAddressDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deletingAddress(
            @PathVariable @Positive(message = "Address ID must be greater than zero") Long addressId){
        log.info("AddressController => deletingAddress : In");
        String status = addressService.deleteAddress(addressId);
        log.info("AddressController => deletingAddress : Out");
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
