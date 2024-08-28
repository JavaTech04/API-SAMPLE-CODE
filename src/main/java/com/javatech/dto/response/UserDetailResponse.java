package com.javatech.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Builder
public class UserDetailResponse implements Serializable {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private String phone;

    private String gender;

    private Date dateOfBirth;

    private String type;

    private String status;
}
