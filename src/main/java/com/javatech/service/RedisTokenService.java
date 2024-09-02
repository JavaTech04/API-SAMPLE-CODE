package com.javatech.service;

import com.javatech.exception.InvalidDataException;
import com.javatech.exception.ResourceNotFoundException;
import com.javatech.model.RedisToken;
import com.javatech.repository.RedisTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenService {
    private final RedisTokenRepository redisTokenRepository;

    public String save(RedisToken redisToken) {
        RedisToken result = redisTokenRepository.save(redisToken);
        return result.getId();
    }

    public void remove(String id) {
        isExists(id);
        redisTokenRepository.deleteById(id);
    }

    public RedisToken getById(String id) {
        return redisTokenRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Redis Token not found!"));
    }

    public boolean isExists(String id) {
        if (redisTokenRepository.existsById(id)) {
            throw new InvalidDataException("Token not exists");
        }
        return true;
    }
}
