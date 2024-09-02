package com.javatech.service;

import com.javatech.dto.requests.UserRequestDTO;
import com.javatech.dto.response.PageResponse;
import com.javatech.dto.response.UserDetailResponse;
import com.javatech.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {

    UserDetailsService userDetailsService();

    User getByUsername(String userName);

    User getUserByEmail(String email);

    long saveUser(UserRequestDTO request);

    long saveUser(User user);

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, String status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

    PageResponse<?> getAllUsersWithSortBy(int pageNo, int pageSize, String sortBy);

    PageResponse<?> getAllUsersWithSortByMultipleColumns(int pageNo, int pageSize, String... sorts);

    PageResponse<?> getAllUsersAndSearchWithPagingAndSorting(int pageNo, int pageSize, String search, String sortBy);

    PageResponse<?> advanceSearchWithCriteria(int pageNo, int pageSize, String sortBy, String address, String... search);

    PageResponse<?> advanceSearchWithSpecifications(Pageable pageable, String[] user, String[] address);
}
