package com.javatech.controller;

import com.javatech.dto.requests.SignInRequest;
import com.javatech.dto.response.TokenResponse;
import com.javatech.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handle auth
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Validated
@RequiredArgsConstructor
@Tag(name = "Authentication controller")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * @param request {'username': 'your_username', 'password': 'your_password', 'platform': 'WEB|ANDROID|IOS', 'deviceToken': 'your_deviceToken', 'version': 'your_version'}
     */
    @Operation(summary = "Access token", description = "Enter a valid username and password to generate the access_token and refresh_token used to log in")
    @PostMapping("/access")
    public ResponseEntity<TokenResponse> access(@RequestBody SignInRequest request) {
        return ResponseEntity.ok(this.authenticationService.authenticate(request));
    }

    /**
     * @param request Header: x-token
     */
    @Operation(summary = "Refresh token", description = "Receives a refresh_token and refreshes the access_token")
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        return ResponseEntity.ok(this.authenticationService.refresh(request));
    }

    /**
     * @param request Header: x-token
     */
    @Operation(summary = "Logout", description = "Receives a refresh_token and remove access_token from database")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        return ResponseEntity.ok(this.authenticationService.logout(request));
    }
}
