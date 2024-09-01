package com.javatech.service.impl;

import com.javatech.service.JwtService;
import com.javatech.utils.TokenType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {
    @Override
    public String generateToken(UserDetails user) {
        return "DUMMY-TOKEN";
    }

    @Override
    public String generateRefreshToken(UserDetails user) {
        return "";
    }

    @Override
    public String extractUsername(String token, TokenType type) {
        return "";
    }

    @Override
    public boolean isValid(String token, TokenType type, UserDetails user) {
        return false;
    }
}
