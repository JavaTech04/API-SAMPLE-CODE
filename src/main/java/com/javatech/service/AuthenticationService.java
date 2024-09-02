package com.javatech.service;

import com.javatech.dto.requests.SignInRequest;
import com.javatech.dto.response.TokenResponse;
import com.javatech.model.Token;
import com.javatech.model.User;
import com.javatech.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.javatech.utils.TokenType.ACCESS_TOKEN;
import static com.javatech.utils.TokenType.REFRESH_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final TokenService tokenService;

    public TokenResponse authenticate(SignInRequest request) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Username or password is incorrect"));

        String accessToken = jwtService.generateToken(user);
        String refresh_token = jwtService.generateRefreshToken(user);

        //Save token to db
        this.tokenService.save(Token.builder().username(user.getUsername()).accessToken(accessToken).refreshToken(refresh_token).build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refresh_token)
                .userId(user.getId())
                .build();
    }

    public TokenResponse refresh(HttpServletRequest request) {
        // Validate token
        String refresh_token = request.getHeader(AUTHORIZATION);
        if (StringUtils.isBlank(refresh_token)) {
            throw new InvalidDataAccessApiUsageException("Token must be not blank!");
        }
        //Extract user from token
        final String username = this.jwtService.extractUsername(refresh_token, REFRESH_TOKEN);
        //Check it into database
        Optional<User> user = this.userRepository.findByUsername(username);
        if (!this.jwtService.isValid(refresh_token, REFRESH_TOKEN, user.get())) {
            throw new InvalidDataAccessApiUsageException("Token is invalid!");
        }
        String access_token = this.jwtService.generateToken(user.get());

        //Save token to db
        this.tokenService.save(Token.builder().username(username).accessToken(access_token).refreshToken(refresh_token).build());
        return TokenResponse.builder()
                .accessToken(access_token)
                .refreshToken(refresh_token)
                .userId(user.get().getId())
                .build();
    }

    public String logout(HttpServletRequest request) {
        // Validate token
        String refresh_token = request.getHeader(AUTHORIZATION);
        if (StringUtils.isBlank(refresh_token)) {
            throw new InvalidDataAccessApiUsageException("Token must be not blank!");
        }
        //Extract user from token
        final String username = this.jwtService.extractUsername(refresh_token, ACCESS_TOKEN);
        //Check token in database
        Token currentToken = this.tokenService.getByUsername(username);
        // Delete token permanent
        this.tokenService.delete(currentToken);
        return "Logout successful";
    }
}
