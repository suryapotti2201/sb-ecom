package com.ecommerce.project.service;

import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repository.AddressRepository;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    private AddressDTO addressDTO;
    private Address address;
    private Address savedAddress;
    private User user;

    @BeforeEach
    void setUp() {
        addressDTO = mock(AddressDTO.class);
        address = mock(Address.class);
        savedAddress = mock(Address.class);
        user = mock(User.class);
    }

    @Test
    void addAddress_shouldAssociateAddressWithLoggedInUserAndReturnMappedDto() {
        List<Address> addresses = new ArrayList<>();
        when(modelMapper.map(addressDTO, Address.class)).thenReturn(address);
        when(authUtil.loggedInUser()).thenReturn(user);
        when(user.getAddresses()).thenReturn(addresses);
        when(user.getUserName()).thenReturn("user@example.com");
        when(addressRepository.save(address)).thenReturn(savedAddress);

        AddressDTO result = mock(AddressDTO.class);
        when(modelMapper.map(savedAddress, AddressDTO.class)).thenReturn(result);

        AddressDTO actual = addressService.addAddress(addressDTO);

        assertSame(result, actual);
        assertEquals(List.of(address), addresses);
        verify(user).setAddresses(addresses);
        verify(address).setUser(user);
        verify(address).setCreatedBy("user@example.com");
        verify(address).setLastUpdatedBy("user@example.com");
        verify(address).setCreatedOn(any());
        verify(address).setLastUpdatedOn(any());
        verify(addressRepository).save(address);
        verify(modelMapper).map(savedAddress, AddressDTO.class);
    }

    @Test
    void getAddresses_shouldReturnMappedAddresses() {
        Address secondAddress = mock(Address.class);
        AddressDTO firstDto = mock(AddressDTO.class);
        AddressDTO secondDto = mock(AddressDTO.class);

        when(addressRepository.findAll()).thenReturn(List.of(address, secondAddress));
        when(modelMapper.map(address, AddressDTO.class)).thenReturn(firstDto);
        when(modelMapper.map(secondAddress, AddressDTO.class)).thenReturn(secondDto);

        List<AddressDTO> result = addressService.getAddresses();

        assertEquals(List.of(firstDto, secondDto), result);
        verify(addressRepository).findAll();
        verify(modelMapper).map(address, AddressDTO.class);
        verify(modelMapper).map(secondAddress, AddressDTO.class);
    }

    @Test
    void getAddresses_shouldReturnEmptyListWhenRepositoryReturnsNoAddresses() {
        when(addressRepository.findAll()).thenReturn(Collections.emptyList());

        List<AddressDTO> result = addressService.getAddresses();

        assertEquals(Collections.emptyList(), result);
        verify(addressRepository).findAll();
        verifyNoInteractions(modelMapper);
    }

    @Test
    void getAddress_shouldReturnMappedAddressForLoggedInEmail() {
        Long addressId = 12L;
        String email = "user@example.com";
        AddressDTO resultDto = mock(AddressDTO.class);

        when(authUtil.loggedInEmailId()).thenReturn(email);
        when(addressRepository.findByIdAndEmailId(addressId, email))
                .thenReturn(Optional.of(address));
        when(modelMapper.map(address, AddressDTO.class)).thenReturn(resultDto);

        AddressDTO result = addressService.getAddress(addressId);

        assertSame(resultDto, result);
        verify(addressRepository).findByIdAndEmailId(addressId, email);
        verify(modelMapper).map(address, AddressDTO.class);
    }

    @Test
    void getAddress_shouldThrowWhenAddressDoesNotExistForLoggedInEmail() {
        Long addressId = 12L;
        String email = "user@example.com";

        when(authUtil.loggedInEmailId()).thenReturn(email);
        when(addressRepository.findByIdAndEmailId(addressId, email))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> addressService.getAddress(addressId));

        verify(addressRepository).findByIdAndEmailId(addressId, email);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void getAddressForLoggedInUser_shouldReturnMappedAddresses() {
        String email = "user@example.com";
        Address secondAddress = mock(Address.class);
        AddressDTO firstDto = mock(AddressDTO.class);
        AddressDTO secondDto = mock(AddressDTO.class);

        when(authUtil.loggedInEmailId()).thenReturn(email);
        when(addressRepository.findByEmail(email)).thenReturn(List.of(address, secondAddress));
        when(modelMapper.map(address, AddressDTO.class)).thenReturn(firstDto);
        when(modelMapper.map(secondAddress, AddressDTO.class)).thenReturn(secondDto);

        List<AddressDTO> result = addressService.getAddressForLoggedInUser();

        assertEquals(List.of(firstDto, secondDto), result);
        verify(addressRepository).findByEmail(email);
        verify(modelMapper).map(address, AddressDTO.class);
        verify(modelMapper).map(secondAddress, AddressDTO.class);
    }

    @Test
    void getAddressForLoggedInUser_shouldReturnEmptyListWhenNoAddressesExist() {
        String email = "user@example.com";

        when(authUtil.loggedInEmailId()).thenReturn(email);
        when(addressRepository.findByEmail(email)).thenReturn(Collections.emptyList());

        List<AddressDTO> result = addressService.getAddressForLoggedInUser();

        assertEquals(Collections.emptyList(), result);
        verify(addressRepository).findByEmail(email);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void updateAddress_shouldUpdateAddressAndUserAssociations() {
        Long addressId = 12L;
        String email = "user@example.com";
        String userName = "user@example.com";
        List<Address> addresses = new ArrayList<>();
        Address existingAddress = mock(Address.class);
        AddressDTO resultDto = mock(AddressDTO.class);

        when(authUtil.loggedInEmailId()).thenReturn(email);
        when(authUtil.loggedInUser()).thenReturn(user);
        when(user.getUserName()).thenReturn(userName);
        when(addressRepository.findByIdAndEmailId(addressId, email))
                .thenReturn(Optional.of(address));
        when(address.getUser()).thenReturn(user);
        when(address.getAddressId()).thenReturn(addressId);
        when(user.getAddresses()).thenReturn(addresses);
        when(addressRepository.save(address)).thenReturn(savedAddress);
        when(modelMapper.map(savedAddress, AddressDTO.class)).thenReturn(resultDto);

        addresses.add(existingAddress);
        addresses.add(address);

        AddressDTO result = addressService.updateAddress(addressId, addressDTO);

        assertSame(resultDto, result);
        verify(address).setCity(any());
        verify(address).setPincode(any());
        verify(address).setCountry(any());
        verify(address).setStreet(any());
        verify(address).setBuildingName(any());
        verify(address).setLastUpdatedBy(userName);
        verify(address).setLastUpdatedOn(any());
        verify(addressRepository).save(address);
        verify(userRepository).save(user);
        verify(user).setLastUpdatedBy(userName);
        verify(user).setLastUpdatedOn(any());
        assertSame(savedAddress, addresses.get(1));
    }

    @Test
    void updateAddress_shouldThrowWhenAddressDoesNotExist() {
        Long addressId = 12L;
        String email = "user@example.com";

        when(authUtil.loggedInEmailId()).thenReturn(email);
        when(addressRepository.findByIdAndEmailId(addressId, email))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> addressService.updateAddress(addressId, addressDTO));

        verify(addressRepository).findByIdAndEmailId(addressId, email);
        verify(addressRepository, never()).save(any());
        verifyNoInteractions(userRepository);
    }

    @Test
    void deleteAddress_shouldRemoveAddressSaveUserDeleteAddressAndReturnMessage() {
        Long addressId = 12L;
        String email = "user@example.com";
        String userName = "user@example.com";
        Address anotherAddress = mock(Address.class);
        List<Address> addresses = new ArrayList<>();

        when(authUtil.loggedInEmailId()).thenReturn(email);
        when(authUtil.loggedInUser()).thenReturn(user);
        when(user.getUserName()).thenReturn(userName);
        when(addressRepository.findByIdAndEmailId(addressId, email))
                .thenReturn(Optional.of(address));
        when(address.getUser()).thenReturn(user);
        when(address.getAddressId()).thenReturn(addressId);
        when(anotherAddress.getAddressId()).thenReturn(99L);
        when(user.getAddresses()).thenReturn(addresses);

        addresses.add(address);
        addresses.add(anotherAddress);

        String result = addressService.deleteAddress(addressId);

        assertEquals("Deleted the Address Successfully!", result);
        assertEquals(List.of(anotherAddress), addresses);
        verify(user).setLastUpdatedBy(userName);
        verify(user).setLastUpdatedOn(any());
        verify(userRepository).save(user);
        verify(addressRepository).deleteById(addressId);
    }

    @Test
    void deleteAddress_shouldThrowWhenAddressDoesNotExist() {
        Long addressId = 12L;
        String email = "user@example.com";

        when(authUtil.loggedInEmailId()).thenReturn(email);
        when(addressRepository.findByIdAndEmailId(addressId, email))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> addressService.deleteAddress(addressId));

        verify(addressRepository).findByIdAndEmailId(addressId, email);
        verify(addressRepository, never()).deleteById(any());
        verifyNoInteractions(userRepository);
    }
}
