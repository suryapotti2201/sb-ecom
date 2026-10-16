package com.ecommerce.project.controller;

import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.service.AddressService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    @Test
    void addAddressReturnsCreatedAddressWithCreatedStatus() {
        AddressDTO request = org.mockito.Mockito.mock(AddressDTO.class);
        AddressDTO createdAddress = org.mockito.Mockito.mock(AddressDTO.class);
        when(addressService.addAddress(request)).thenReturn(createdAddress);

        ResponseEntity<AddressDTO> response = addressController.addAddress(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(createdAddress, response.getBody());
        verify(addressService).addAddress(request);
    }

    @Test
    void addAddressPropagatesServiceException() {
        AddressDTO request = org.mockito.Mockito.mock(AddressDTO.class);
        RuntimeException failure = new RuntimeException("service failure");
        when(addressService.addAddress(request)).thenThrow(failure);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> addressController.addAddress(request)
        );

        assertSame(failure, thrown);
        verify(addressService).addAddress(request);
    }

    @Test
    void getAddressesReturnsAllAddressesWithOkStatus() {
        List<AddressDTO> addresses = List.of(
                org.mockito.Mockito.mock(AddressDTO.class),
                org.mockito.Mockito.mock(AddressDTO.class)
        );
        when(addressService.getAddresses()).thenReturn(addresses);

        ResponseEntity<List<AddressDTO>> response = addressController.getAddresses();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(addresses, response.getBody());
        verify(addressService).getAddresses();
    }

    @Test
    void getAddressReturnsAddressWithOkStatus() {
        Long addressId = 1L;
        AddressDTO address = org.mockito.Mockito.mock(AddressDTO.class);
        when(addressService.getAddress(addressId)).thenReturn(address);

        ResponseEntity<AddressDTO> response = addressController.getAddress(addressId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(address, response.getBody());
        verify(addressService).getAddress(addressId);
    }

    @Test
    void getAddressForLoggedInUserReturnsUserAddressesWithOkStatus() {
        List<AddressDTO> addresses = List.of(org.mockito.Mockito.mock(AddressDTO.class));
        when(addressService.getAddressForLoggedInUser()).thenReturn(addresses);

        ResponseEntity<List<AddressDTO>> response =
                addressController.getAddressForLoggedInUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(addresses, response.getBody());
        verify(addressService).getAddressForLoggedInUser();
    }

    @Test
    void updateAddressReturnsUpdatedAddressWithOkStatus() {
        Long addressId = 2L;
        AddressDTO request = org.mockito.Mockito.mock(AddressDTO.class);
        AddressDTO updatedAddress = org.mockito.Mockito.mock(AddressDTO.class);
        when(addressService.updateAddress(addressId, request)).thenReturn(updatedAddress);

        ResponseEntity<AddressDTO> response =
                addressController.updateAddress(addressId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updatedAddress, response.getBody());
        verify(addressService).updateAddress(addressId, request);
    }

    @Test
    void deletingAddressReturnsServiceStatusWithOkStatus() {
        Long addressId = 3L;
        String status = "Address deleted successfully";
        when(addressService.deleteAddress(addressId)).thenReturn(status);

        ResponseEntity<String> response = addressController.deletingAddress(addressId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(status, response.getBody());
        verify(addressService).deleteAddress(addressId);
    }
}
