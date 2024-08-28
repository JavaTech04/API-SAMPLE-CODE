package com.javatech.service;

import com.javatech.dto.requests.UserRequestDTO;
import com.javatech.dto.response.UserDetailResponse;

public interface UserService {
    long saveUser(UserRequestDTO request);

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, String status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

}
