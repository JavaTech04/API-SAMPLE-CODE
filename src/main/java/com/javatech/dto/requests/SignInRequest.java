package com.javatech.dto.requests;

import com.javatech.utils.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignInRequest {
    @NotBlank(message = "Username must be not null")
    private String username;
    @NotBlank(message = "Password must be not blank")
    private String password;

    //No require
    @NotNull(message = "Platform must be not null")
    private Platform platform;
    private String deviceToken;
    private String version;
}