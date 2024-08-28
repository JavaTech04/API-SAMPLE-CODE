package com.javatech.controller;

import com.javatech.configuration.Translator;
import com.javatech.dto.requests.UserRequestDTO;
import com.javatech.dto.response.ResponseData;
import com.javatech.dto.response.ResponseError;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("api/v1/user")
@Validated
public class UserController {
    @PostMapping
    public ResponseData<?> addUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return new ResponseData<>(HttpStatus.OK.value(), Translator.toLocale("user.add.success"), 1);
    }

    @PutMapping("/{id}")
    public ResponseData<?> updateUser(@PathVariable String id, @RequestBody UserRequestDTO userRequestDTO) {
        return new ResponseData<>(HttpStatus.OK.value(), Translator.toLocale("user.update.success"));
    }

    @PatchMapping("/{id}")
    public ResponseData<?> changeStatus(@PathVariable int id, @RequestParam boolean status) {
        return new ResponseData<>(HttpStatus.OK.value(), Translator.toLocale("user.changeStatus.success"));
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> deleteUser(@PathVariable int id) {
        return new ResponseData<>(HttpStatus.OK.value(), Translator.toLocale("user.delete.success"));
    }

    @GetMapping("/{id}")
    public ResponseData<?> getUser(@PathVariable @Min(1) int id) {
        UserRequestDTO user = UserRequestDTO.builder()
                .email("nonghoangvu04@gmail.com")
                .phone("0123456789")
                .firstName("Vu")
                .lastName("Nong Hoang")
                .gender("male")
                .dateOfBirth(new Date())
                .build();
        return new ResponseData<>(HttpStatus.OK.value(), "Request get user successfully", user);
    }
}
