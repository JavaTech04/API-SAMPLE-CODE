package com.javatech.service;

import com.javatech.exception.ResourceNotFoundException;
import com.javatech.model.Token;
import com.javatech.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public record TokenService(TokenRepository tokenRepository) {
    /**
     * Get token by username
     *
     * @param username
     * @return token
     */
    public Token getByUsername(String username) {
        return tokenRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Not found token"));
    }

    /**
     * Save token to DB
     *
     * @param token
     * @return
     */
    public int save(Token token) {
        Optional<Token> optional = tokenRepository.findByUsername(token.getUsername());
        if (optional.isEmpty()) {
            tokenRepository.save(token);
            return token.getId();
        } else {
            Token t = optional.get();
            t.setAccessToken(token.getAccessToken());
            t.setRefreshToken(token.getRefreshToken());
            tokenRepository.save(t);
            return t.getId();
        }
    }

    /**
     * Delete token by username
     *
     * @param token
     */
    public String delete(Token token) {
        this.tokenRepository.delete(token);
        return "Deleted token: " + token.getAccessToken();
    }
}
