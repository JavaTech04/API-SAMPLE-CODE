package com.javatech.service.impl;

import com.javatech.dto.requests.UserRequestDTO;
import com.javatech.dto.response.UserDetailResponse;
import com.javatech.exception.ResourceNotFoundException;
import com.javatech.model.Address;
import com.javatech.model.User;
import com.javatech.repository.UserRepository;
import com.javatech.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public long saveUser(UserRequestDTO request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .status(request.getStatus())
                .type(request.getType())
                .build();
        request.getAddresses().forEach(a ->
                user.saveAddress(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build()));
        this.userRepository.save(user);
        log.info("User has save!");
        return user.getId();
    }

    @Override
    public void updateUser(long userId, UserRequestDTO request) {
        // This test case will have errors - not yet resolved
        User user = getUserById(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        if (!request.getEmail().equals(user.getEmail())) {
            // check email from database if not exist then allow update email otherwise throw exception
            user.setEmail(request.getEmail());
        }
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setStatus(request.getStatus());
        user.setType(request.getType());
        request.getAddresses().forEach(a ->
                user.saveAddress(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build()));
        userRepository.save(user);
        log.info("User has updated successfully, userId={}", userId);
    }

    @Override
    public void changeStatus(long userId, String status) {
        User user = getUserById(userId);
        user.setStatus(status);
        userRepository.save(user);
        log.info("User status has changed successfully, userId={}", userId);
    }

    @Override
    public void deleteUser(long userId) {
        getUserById(userId);
        userRepository.deleteById(userId);
        log.info("User has deleted permanent successfully, userId={}", userId);
    }

    @Override
    public UserDetailResponse getUser(long userId) {
        User user = getUserById(userId);
        return UserDetailResponse.builder()
                .id(userId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .phone(user.getPhone())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .type(user.getType())
                .build();
    }

    public User getUserById(long userId) {
        return this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
    }
}
