package com.javatech.service;

import com.javatech.dto.requests.SignInRequest;
import com.javatech.dto.response.TokenResponse;
import com.javatech.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public TokenResponse authenticate(SignInRequest request) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Username or password is incorrect"));

        String accessToken = jwtService.generateToken(user);
        String refresh_token = jwtService.generateRefreshToken(user);

        //Save token to db

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refresh_token)
                .userId(user.getId())
                .build();
    }

    public TokenResponse refresh(HttpServletRequest request) {


        return TokenResponse.builder()
                .accessToken("access_token")
                .refreshToken("refresh_token")
                .userId(1L)
                .build();
    }

    public String logout(HttpServletRequest request) {

        return "Logout successful";
    }
}
