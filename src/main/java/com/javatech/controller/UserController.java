package com.javatech.controller;

import com.javatech.dto.UserRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("api/v1/user")
@Validated
public class UserController {
    @PostMapping
    public String addUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return "User added";
    }

    @PutMapping("/{id}")
    public String updateUser(@PathVariable String id, @RequestBody UserRequestDTO userRequestDTO) {
        return "User updated";
    }

    @PatchMapping("/{id}")
    public String changeStatus(@PathVariable int id, @RequestParam boolean status) {
        return "User status changed to " + status;
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id) {
        return "User deleted " + id;
    }

    @GetMapping("/{id}")
    public UserRequestDTO getUser(@PathVariable @Min(1) int id) {
        return UserRequestDTO.builder()
                .email("nonghoangvu04@gmail.com")
                .phone("+84 777 04 085")
                .firstName("Vu")
                .lastName("Nong Hoang")
                .dateOfBirth(new Date())
                .build();
    }


}
