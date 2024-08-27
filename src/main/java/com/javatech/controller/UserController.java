package com.javatech.controller;

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
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "User added successfully", 1);
            // throw new RuntimeException("Error something went wrong");
        } catch (Exception e) {
            return new ResponseError<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseData<?> updateUser(@PathVariable String id, @RequestBody UserRequestDTO userRequestDTO) {
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "User updated successfully");
        } catch (Exception e) {
            return new ResponseError<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseData<?> changeStatus(@PathVariable int id, @RequestParam boolean status) {
        try {
            return new ResponseData<>(HttpStatus.OK.value(), String.format("User status changed to %b", status));
        } catch (Exception e) {
            return new ResponseError<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> deleteUser(@PathVariable int id) {
        try {
            return new ResponseData<>(HttpStatus.OK.value(), String.format("User deleted successfully with id %d", id));
        } catch (Exception e) {
            return new ResponseError<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseData<?> getUser(@PathVariable @Min(1) int id) {
        UserRequestDTO user = UserRequestDTO.builder()
                .email("nonghoangvu04@gmail.com")
                .phone("+84 777 04 085")
                .firstName("Vu")
                .lastName("Nong Hoang")
                .gender("male")
                .dateOfBirth(new Date())
                .build();
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Request get user successfully", user);
        } catch (Exception e) {
            return new ResponseError<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }


}
