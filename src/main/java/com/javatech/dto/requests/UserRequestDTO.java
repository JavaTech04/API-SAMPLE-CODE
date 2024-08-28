package com.javatech.dto.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javatech.dto.validate.EnumValue;
import com.javatech.dto.validate.PhoneNumber;
import com.javatech.utils.Gender;
import com.javatech.utils.UserStatus;
import com.javatech.utils.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder
public class UserRequestDTO implements Serializable {
    @NotBlank(message = "firstName must be not blank")
    private String firstName;

    @NotBlank(message = "lastName must be not blank")
    private String lastName;

    @Email(message = "email invalid format")
    private String email;

    @NotNull(message = "username must be not null")
    private String username;

    @NotNull(message = "password must be not null")
    private String password;

//    @Pattern(regexp = "^\\d{10}$", message = "phone invalid format")
    @PhoneNumber
    private String phone;

    @NotNull(message = "gender must be not null")
    @EnumValue(name = "gender", enumClass = Gender.class, message = "Invalid gender")
    private String gender;

    @NotNull(message = "gender must be not null")
    @EnumValue(name = "gender", enumClass = UserStatus.class, message = "Invalid gender")
    private String status;

    @NotNull(message = "type must be not null")
    @EnumValue(name = "type", enumClass = UserType.class)
    private String type;

    @NotNull(message = "dateOfBirth must be not null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date dateOfBirth;

    @NotEmpty(message = "addresses can not empty")
    private Set<AddressDTO> addresses;
}
