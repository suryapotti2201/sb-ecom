package com.ecommerce.project.service;

import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repository.AddressRepository;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public AddressDTO addAddress(AddressDTO addressDTO) {
        Address address = modelMapper.map(addressDTO, Address.class);
        User user = authUtil.loggedInUser();
        List<Address> addresses =  user.getAddresses();
        addresses.add(address);
        user.setAddresses(addresses);
        address.setUser(user);
        address.setCreatedBy(user.getUserName());
        address.setLastUpdatedBy(user.getUserName());
        address.setCreatedOn(LocalDateTime.now());
        address.setLastUpdatedOn(LocalDateTime.now());
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream().map(
                address -> modelMapper.map(address, AddressDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO getAddress(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(
                () -> new ResourceNotFoundException("Address", "addressId", addressId));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressForLoggedInUser() {
        String email = authUtil.loggedInEmailId();
        List<Address> addresses = addressRepository.findByEmail(email);
        return addresses.stream().map(
                        address -> modelMapper.map(address, AddressDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address addressFromDataBase = addressRepository.findById(addressId).orElseThrow(
                () -> new ResourceNotFoundException("Address", "addressId", addressId));
        addressFromDataBase.setCity(addressDTO.getCity());
        addressFromDataBase.setPincode(addressDTO.getPincode());
        addressFromDataBase.setCountry(addressDTO.getCountry());
        addressFromDataBase.setStreet(addressDTO.getStreet());
        addressFromDataBase.setBuildingName(addressDTO.getBuildingName());
        addressFromDataBase.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        addressFromDataBase.setLastUpdatedOn(LocalDateTime.now());
        Address updatedAddress = addressRepository.save(addressFromDataBase);

        User user = addressFromDataBase.getUser();
        user.getAddresses().removeIf(item -> item.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        user.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        user.setLastUpdatedOn(LocalDateTime.now());
        userRepository.save(user);
        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address addressFromDataBase = addressRepository.findById(addressId).orElseThrow(
                () -> new ResourceNotFoundException("Address", "addressId", addressId));
        User user = addressFromDataBase.getUser();
        user.getAddresses().removeIf(item -> item.getAddressId().equals(addressId));
        user.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        user.setLastUpdatedOn(LocalDateTime.now());
        userRepository.save(user);
        addressRepository.deleteById(addressId);
        return "Deleted the Address Successfully!";
    }
}
