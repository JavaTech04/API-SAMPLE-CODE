package com.javatech.controller;

import com.javatech.configuration.Translator;
import com.javatech.dto.requests.UserRequestDTO;
import com.javatech.dto.response.ResponseData;
import com.javatech.dto.response.ResponseError;
import com.javatech.dto.response.UserDetailResponse;
import com.javatech.exception.ResourceNotFoundException;
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

    /**
     * @param userRequestDTO
     * @return
     */
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

    /**
     * @param id
     * @param userRequestDTO
     * @return
     */
    @Operation(summary = "Update user", description = "Send a request via this API to update user")
    @PutMapping("/{id}")
    public ResponseData<?> updateUser(@PathVariable long id, @RequestBody UserRequestDTO userRequestDTO) {
        log.info("Request update user: {}", userRequestDTO.getUsername());
        try {
            this.userService.updateUser(id, userRequestDTO);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.update.success"));
        } catch (ResourceNotFoundException e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError<>(HttpStatus.OK.value(), e.getMessage());
        }
    }

    /**
     * @param id
     * @param status
     * @return
     */
    @Operation(summary = "Change status of user", description = "Send a request via this API to change status of user")
    @PatchMapping("/{id}")
    public ResponseData<?> changeStatus(@PathVariable long id, @RequestParam String status) {
        log.info("Request update status user: {}", id);
        try {
            this.userService.changeStatus(id, status);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.changeStatus.success"));
        } catch (ResourceNotFoundException e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError<>(HttpStatus.OK.value(), e.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @Operation(summary = "Delete user permanently", description = "Send a request via this API to delete user permanently")
    @DeleteMapping("/{id}")
    public ResponseData<?> deleteUser(@PathVariable int id) {
        log.info("Request delete user id: {}", id);
        try {
            this.userService.deleteUser(id);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), Translator.toLocale("user.delete.success"));
        } catch (ResourceNotFoundException e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError<>(HttpStatus.OK.value(), e.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @Operation(summary = "Get user by id", description = "Send a request via this API to get user information")
    @GetMapping("/{id}")
    public ResponseData<?> getUser(@PathVariable @Min(1) long id) {
        log.info("Request find user: {}", id);
        try {
            UserDetailResponse user = this.userService.getUser(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Request get user successfully", user);
        } catch (ResourceNotFoundException e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError<>(HttpStatus.OK.value(), e.getMessage());
        }

    }

    /**
     * @param pageNo
     * @param pageSize
     * @param sortBy
     * @return
     */
    @Operation(summary = "Get list of per pageNo", description = "Send a request via this API to get user list by pageNo and pageSize")
    @GetMapping("/list")
    public ResponseData<?> getAllUsersWithSortBy(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
            @RequestParam(required = false) String sortBy
    ) {
        log.info("Request get all of users");
        return new ResponseData<>(HttpStatus.OK.value(), "Request get user successfully", this.userService.getAllUsersWithSortBy(pageNo, pageSize, sortBy));
    }

    /**
     * @param pageNo
     * @param pageSize
     * @param sort
     * @return
     */
    @Operation(summary = "Get list of users with sort by multiple columns", description = "Send a request via this API to get user list by pageNo, pageSize and sort by multiple column")
    @GetMapping("/list-with-sort-by-multiple-columns")
    public ResponseData<?> getAllUsersWithSortByMultipleColumns(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
            @RequestParam(required = false) String... sort
    ) {
        log.info("Request get all of users with sort by multiple columns");
        return new ResponseData<>(HttpStatus.OK.value(), "Request get user successfully", this.userService.getAllUsersWithSortByMultipleColumns(pageNo, pageSize, sort));
    }

    /**
     * @param pageNo
     * @param pageSize
     * @param search
     * @param sortBy
     * @return
     */
    @Operation(summary = "Get list of users and search with paging and sorting by customize query", description = "Send a request via this API to get user list by pageNo, pageSize and sort by multiple column")
    @GetMapping("/list-user-and-search-with-paging-and-sorting")
    public ResponseData<?> getAllUsersAndSearchWithPagingAndSorting(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy
    ) {
        log.info("Request get list of users and search with paging and sorting");
        return new ResponseData<>(HttpStatus.OK.value(), "Request get user successfully", this.userService.getAllUsersAndSearchWithPagingAndSorting(pageNo, pageSize, search, sortBy));
    }
}
