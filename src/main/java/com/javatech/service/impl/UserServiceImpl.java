package com.javatech.service.impl;

import com.javatech.dto.requests.UserRequestDTO;
import com.javatech.dto.response.*;
import com.javatech.exception.ResourceNotFoundException;
import com.javatech.model.*;
import com.javatech.repository.*;
import com.javatech.repository.specification.UserSpecificationsBuilder;
import com.javatech.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.*;

import static com.javatech.utils.AppConst.SEARCH_SPEC_OPERATOR;
import static com.javatech.utils.AppConst.SORT_BY;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final SearchRepository repository;

    /**
     * @param request
     * @return
     */
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

    /**
     * @param userId
     * @param request
     */
    @Override
    public void updateUser(long userId, UserRequestDTO request) {
        // This test case will have errors - not yet resolved
        User user = getUserById(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        if (!request.getEmail().equals(user.getEmail())) { // check email from database if not exist then allow update email otherwise throw exception
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

    /**
     * @param userId
     * @param status
     */
    @Override
    public void changeStatus(long userId, String status) {
        User user = getUserById(userId);
        user.setStatus(status);
        userRepository.save(user);
        log.info("User status has changed successfully, userId={}", userId);
    }

    /**
     * @param userId
     */
    @Override
    public void deleteUser(long userId) {
        getUserById(userId);
        userRepository.deleteById(userId);
        log.info("User has deleted permanent successfully, userId={}", userId);
    }

    /**
     * @param userId
     * @return
     */
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

    /**
     * @param pageNo
     * @param pageSize
     * @param sortBy
     * @return
     */
    @Override
    public PageResponse<?> getAllUsersWithSortBy(int pageNo, int pageSize, String sortBy) {
        int page = 0;
        if (pageNo > 0) {
            page = pageNo - 1;
        }
        List<Sort.Order> orders = new ArrayList<>();
        if (StringUtils.hasLength(sortBy)) {
            // firstName:asc|desc
            Pattern pattern = Pattern.compile(SORT_BY);
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(orders));
        Page<User> user = this.userRepository.findAll(pageable);
        return converToPageResponse(user, pageable);

    }

    /**
     * @param pageNo
     * @param pageSize
     * @param sorts
     * @return
     */
    @Override
    public PageResponse<?> getAllUsersWithSortByMultipleColumns(int pageNo, int pageSize, String... sorts) {
        int page = 0;
        if (pageNo > 0) {
            page = pageNo - 1;
        }
        List<Sort.Order> orders = new ArrayList<>();

        for (String sortBy : sorts) {
            // firstName:asc|desc
            Pattern pattern = Pattern.compile(SORT_BY);
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(orders));
        Page<User> user = this.userRepository.findAll(pageable);
        return converToPageResponse(user, pageable);
    }

    /**
     * @param pageNo
     * @param pageSize
     * @param search
     * @param sortBy
     * @return
     */
    @Override
    public PageResponse<?> getAllUsersAndSearchWithPagingAndSorting(int pageNo, int pageSize, String search, String sortBy) {
        return this.repository.getAllUsersAndSearchWithPagingAndSorting(pageNo, pageSize, search, sortBy);
    }

    @Override
    public PageResponse<?> advanceSearchWithCriteria(int pageNo, int pageSize, String sortBy, String address, String... search) {
        return this.repository.searchUserByCriteria(pageNo, pageSize, sortBy, address, search);
    }

    @Override
    public PageResponse<?> advanceSearchWithSpecifications(Pageable pageable, String[] user, String[] address) {
        log.info("====================== getUsersBySpecifications ======================");
        if (user != null && address != null) { // Search on user and address => join table
            return repository.searchUserByCriteriaWithJoin(pageable, user, address);
        } else if (user != null) { // Search on user => no join
            UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
            Pattern pattern = Pattern.compile(SEARCH_SPEC_OPERATOR);
            for (String s : user) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(4), matcher.group(3), matcher.group(5));
                }
            }
            Page<User> users = userRepository.findAll(Objects.requireNonNull(builder.build()), pageable);
            return converToPageResponse(users, pageable);
        }
        return converToPageResponse(this.userRepository.findAll(pageable), pageable);
    }


    /**
     * @param user
     * @param pageable
     * @return
     */
    private PageResponse<?> converToPageResponse(Page<User> user, Pageable pageable) {
        List<?> response = user.stream().map(u -> UserDetailResponse.builder()
                .id(u.getId())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .dateOfBirth(u.getDateOfBirth())
                .gender(u.getGender())
                .phone(u.getPhone())
                .email(u.getEmail())
                .username(u.getUsername())
                .status(u.getStatus())
                .type(u.getType())
                .build()).toList();
        return PageResponse.builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalPages(user.getTotalPages())
                .items(response)
                .build();
    }

    /**
     * @param userId
     * @return
     */
    public User getUserById(long userId) {
        return this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
    }
}
