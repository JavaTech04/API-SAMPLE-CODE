package com.javatech.controller;

import com.javatech.dto.UserRequestDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    @PostMapping
    public String addUser(@RequestBody UserRequestDTO userRequestDTO) {
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
    public UserRequestDTO getUser(@PathVariable int id) {
        return UserRequestDTO.builder()
                .email("nonghoangvu04@gmail.com")
                .phone("+84 777 04 085")
                .firstName("Vu")
                .lastName("Nong Hoang")
                .build();
    }


}
