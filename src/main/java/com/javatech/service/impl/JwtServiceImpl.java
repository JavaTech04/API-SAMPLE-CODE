package com.javatech.service.impl;


import com.javatech.service.JwtService;
import com.javatech.utils.TokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

import static com.javatech.utils.TokenType.ACCESS_TOKEN;

@Service
@Profile("!prod")
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiryHour}")
    private long expiryHour;

    @Value("${jwt.expiryDay}")
    private long expiryDay;

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.refreshKey}")
    private String refreshKey;

    @Override
    public String generateToken(UserDetails user) {
        return generateToken(new HashMap<>(), user);
    }

    private String generateToken(Map<String, Object> claims, UserDetails user) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * this.expiryHour))
                .signWith(getKey(ACCESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * generate refresh token
     */
    @Override
    public String generateRefreshToken(UserDetails user) {
        return "";
    }

    private Key getKey(TokenType type) {
        byte[] keyBytes;
        if (ACCESS_TOKEN.equals(type)) {
            keyBytes = Decoders.BASE64.decode(this.secretKey);
        } else {
            keyBytes = Decoders.BASE64.decode(this.refreshKey);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Handle Extract Token
     */
    @Override
    public String extractUsername(String token, TokenType type) {
        return extractClaim(token, type, Claims::getSubject);
    }

    private <T> T extractClaim(String token, TokenType type, Function<Claims, T> claimResolver) {
        final Claims claims = extraAllClaim(token, type);
        return claimResolver.apply(claims);
    }

    private Claims extraAllClaim(String token, TokenType type) {
        return Jwts.parserBuilder().setSigningKey(getKey(type)).build().parseClaimsJws(token).getBody();
    }

    /**
     * Validate Token
     */
    @Override
    public boolean isValid(String token, TokenType type, UserDetails user) {
        final String username = this.extractUsername(token, type); // Extract token and get username from token
        return (username.equals(user.getUsername()) && !isTokenExpired(token, type));
    }

    private boolean isTokenExpired(String token, TokenType type) {
        return extracExpirationDate(token, type).before(new Date());
    }

    private Date extracExpirationDate(String token, TokenType type) {
        return extractClaim(token, type, Claims::getExpiration);
    }
}