package com.javatech.service;

import com.javatech.dto.requests.ResetPasswordDTO;
import com.javatech.dto.requests.SignInRequest;
import com.javatech.dto.response.TokenResponse;
import com.javatech.exception.InvalidDataException;
import com.javatech.model.Token;
import com.javatech.model.User;
import com.javatech.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.javatech.utils.TokenType.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final TokenService tokenService;

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

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

    /**
     * Forgot password
     *
     * @param email
     */
    public String forgotPassword(String email) {
        // check email exists or not
        User user = this.userService.getUserByEmail(email);
        if (!user.isEnabled()) {
            throw new InvalidDataException("User is not enabled!");
        }
        // generate reset token
        String reset_token = this.jwtService.generateResetToken(user);
        // TODO send email to user
        String confirmLink = String.format("curl --location 'http://localhost:2004/auth/reset-password' \\\n" +
                "--header 'accept: */*' \\\n" +
                "--header 'Content-Type: application/json' \\\n" +
                "--data '%s'", reset_token);
        log.info("--> confirmLink: {}", confirmLink);
        return "Send request...";
    }

    /**
     * Reset password
     * Confirm
     *
     * @param secretKey
     * @return
     */
    public String resetPassword(String secretKey) {
        log.info("========== resetPassword ==========");
        isValidUserByToken(secretKey);
        return "Confirm url...";
    }

    public String changePassword(@Valid ResetPasswordDTO request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException("Passwords do not match");
        }
        User user = isValidUserByToken(request.getSecretKey());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        this.userService.saveUser(user);
        return "Change password successful";
    }

    private User isValidUserByToken(String secretKey) {
        final String username = this.jwtService.extractUsername(secretKey, RESET_TOKEN);
        var user = this.userRepository.findByUsername(username);
        if (!user.get().isEnabled()) {
            throw new InvalidDataException("User is not enabled!");
        }
        if (!this.jwtService.isValid(secretKey, RESET_TOKEN, user.get())) {
            throw new InvalidDataException("Token is invalid!");
        }
        return user.get();
    }
}
