package com.javatech.controller;

import com.javatech.configuration.Translator;
import com.javatech.dto.requests.UserRequestDTO;
import com.javatech.dto.response.ResponseData;
import com.javatech.dto.response.ResponseError;
import com.javatech.dto.response.UserDetailResponse;
import com.javatech.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/user")
@Validated
@RequiredArgsConstructor
@Tag(name = "User controller")
public class UserController {
    private final UserService userService;

    @Operation(method = "POST", summary = "Add user", description = "API Create new user")
    @PostMapping
    public ResponseData<?> addUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        log.info("Request add user: {}", userRequestDTO.getUsername());
        try {
            long userId = this.userService.saveUser(userRequestDTO);
            return new ResponseData<>(HttpStatus.OK.value(), Translator.toLocale("user.add.success"), userId);
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError<>(HttpStatus.BAD_REQUEST.value(), "User add failed");
        }
    }

    @Operation(summary = "Update user", description = "Send a request via this API to update user")
    @PutMapping("/{id}")
    public ResponseData<?> updateUser(@PathVariable long id, @RequestBody UserRequestDTO userRequestDTO) {
        log.info("Request update user: {}", userRequestDTO.getUsername());
        try {
            this.userService.updateUser(id, userRequestDTO);
            return new ResponseData<>(HttpStatus.OK.value(), Translator.toLocale("user.update.success"));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError<>(HttpStatus.BAD_REQUEST.value(), "User add failed");
        }
    }

    @Operation(summary = "Change status of user", description = "Send a request via this API to change status of user")
    @PatchMapping("/{id}")
    public ResponseData<?> changeStatus(@PathVariable long id, @RequestParam String status) {
        log.info("Request update status user: {}", id);
        try {
            this.userService.changeStatus(id, status);
            return new ResponseData<>(HttpStatus.OK.value(), Translator.toLocale("user.changeStatus.success"));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError<>(HttpStatus.BAD_REQUEST.value(), "User update status failed");
        }
    }

    @Operation(summary = "Delete user permanently", description = "Send a request via this API to delete user permanently")
    @DeleteMapping("/{id}")
    public ResponseData<?> deleteUser(@PathVariable int id) {
        log.info("Request delete user id: {}", id);
        try {
            this.userService.deleteUser(id);
            return new ResponseData<>(HttpStatus.OK.value(), Translator.toLocale("user.delete.success"));
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError<>(HttpStatus.BAD_REQUEST.value(), "User delete failed");
        }
    }

    @Operation(summary = "Get user by id", description = "Send a request via this API to get user information")
    @GetMapping("/{id}")
    public ResponseData<?> getUser(@PathVariable @Min(1) long id) {
        log.info("Request find user: {}", id);
        UserDetailResponse user = this.userService.getUser(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Request get user successfully", user);
    }
}
