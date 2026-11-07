package com.ecommerce.project.service;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
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
    @Transactional
    public AddressDTO addAddress(AddressDTO addressDTO) {
        log.info("AddressServiceImpl => addAddress : In");
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
        log.info("AddressServiceImpl => addAddress : Out");
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        log.info("AddressServiceImpl => getAddresses : In");
        List<Address> addresses = addressRepository.findAll();
        log.info("AddressServiceImpl => getAddresses : Out");
        return addresses.stream().map(
                address -> modelMapper.map(address, AddressDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO getAddress(Long addressId) {
        log.info("AddressServiceImpl => getAddress : In");
        Address address = addressRepository.findByIdAndEmailId(addressId, authUtil.loggedInEmailId()).orElseThrow(() ->
                new ResourceNotFoundException("Address", "addressId", addressId));
        log.info("AddressServiceImpl => getAddress : Out");
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressForLoggedInUser() {
        log.info("AddressServiceImpl => getAddressForLoggedInUser : In");
        String email = authUtil.loggedInEmailId();
        List<Address> addresses = addressRepository.findByEmail(email);
        log.info("AddressServiceImpl => getAddressForLoggedInUser : Out");
        return addresses.stream().map(
                        address -> modelMapper.map(address, AddressDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        log.info("AddressServiceImpl => updateAddress : In");
        Address addressFromDataBase = addressRepository.findByIdAndEmailId(addressId, authUtil.loggedInEmailId()).orElseThrow(() ->
                new ResourceNotFoundException("Address", "addressId", addressId));
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
        log.info("AddressServiceImpl => updateAddress : Out");
        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    @Transactional
    public String deleteAddress(Long addressId) {
        log.info("AddressServiceImpl => deleteAddress : In");
        Address addressFromDataBase = addressRepository.findByIdAndEmailId(addressId, authUtil.loggedInEmailId()).orElseThrow(() ->
                new ResourceNotFoundException("Address", "addressId", addressId));
        User user = addressFromDataBase.getUser();
        user.getAddresses().removeIf(item -> item.getAddressId().equals(addressId));
        user.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        user.setLastUpdatedOn(LocalDateTime.now());
        userRepository.save(user);
        addressRepository.deleteById(addressId);
        log.info("AddressServiceImpl => deleteAddress : Out");
        return "Deleted the Address Successfully!";
    }
}
