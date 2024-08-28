package com.javatech.controller;

import com.javatech.configuration.Translator;
import com.javatech.dto.requests.UserRequestDTO;
import com.javatech.dto.response.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/user")
@Validated

@Tag(name = "User controller")
public class UserController {

    @Operation(method = "POST", summary = "Add user", description = "API Create new user")
    @PostMapping
    public ResponseData<?> addUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return new ResponseData<>(HttpStatus.OK.value(), Translator.toLocale("user.add.success"), 1);
    }

    @Operation(summary = "Update user", description = "Send a request via this API to update user")
    @PutMapping("/{id}")
    public ResponseData<?> updateUser(@PathVariable String id, @RequestBody UserRequestDTO userRequestDTO) {
        return new ResponseData<>(HttpStatus.OK.value(), Translator.toLocale("user.update.success"));
    }

    @Operation(summary = "Change status of user", description = "Send a request via this API to change status of user")
    @PatchMapping("/{id}")
    public ResponseData<?> changeStatus(@PathVariable int id, @RequestParam boolean status) {
        return new ResponseData<>(HttpStatus.OK.value(), Translator.toLocale("user.changeStatus.success"));
    }

    @Operation(summary = "Delete user permanently", description = "Send a request via this API to delete user permanently")
    @DeleteMapping("/{id}")
    public ResponseData<?> deleteUser(@PathVariable int id) {
        return new ResponseData<>(HttpStatus.OK.value(), Translator.toLocale("user.delete.success"));
    }

    @Operation(summary = "Get user by id", description = "Send a request via this API to get user information")
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
