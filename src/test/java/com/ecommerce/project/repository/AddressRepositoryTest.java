package com.ecommerce.project.repository;

import com.ecommerce.project.model.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressRepositoryTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private Address address;

    @Test
    void findByEmailReturnsMatchingAddresses() {
        String email = "customer@example.com";
        List<Address> expectedAddresses = List.of(address);

        when(addressRepository.findByEmail(email)).thenReturn(expectedAddresses);

        List<Address> result = addressRepository.findByEmail(email);

        assertSame(expectedAddresses, result);
        verify(addressRepository).findByEmail(email);
    }

    @Test
    void findByEmailReturnsEmptyListWhenNoAddressesMatch() {
        String email = "customer@example.com";
        List<Address> expectedAddresses = List.of();

        when(addressRepository.findByEmail(email)).thenReturn(expectedAddresses);

        List<Address> result = addressRepository.findByEmail(email);

        assertEquals(expectedAddresses, result);
        verify(addressRepository).findByEmail(email);
    }

    @Test
    void findByIdAndEmailIdReturnsMatchingAddress() {
        Long addressId = 42L;
        String emailId = "customer@example.com";
        Optional<Address> expectedAddress = Optional.of(address);

        when(addressRepository.findByIdAndEmailId(addressId, emailId))
                .thenReturn(expectedAddress);

        Optional<Address> result = addressRepository.findByIdAndEmailId(addressId, emailId);

        assertSame(expectedAddress, result);
        verify(addressRepository).findByIdAndEmailId(addressId, emailId);
    }

    @Test
    void findByIdAndEmailIdReturnsEmptyWhenAddressDoesNotBelongToUser() {
        Long addressId = 42L;
        String emailId = "customer@example.com";
        Optional<Address> expectedResult = Optional.empty();

        when(addressRepository.findByIdAndEmailId(addressId, emailId))
                .thenReturn(expectedResult);

        Optional<Address> result = addressRepository.findByIdAndEmailId(addressId, emailId);

        assertEquals(expectedResult, result);
        verify(addressRepository).findByIdAndEmailId(addressId, emailId);
    }
}
