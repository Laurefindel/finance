package com.laurefindel.finance.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laurefindel.finance.dto.UserRequestDto;
import com.laurefindel.finance.dto.UserResponseDto;
import com.laurefindel.finance.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@Validated
@Tag(name = "Users", description = "User management endpoints")
public class UserController {
    private final UserService service;
    
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/all")
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    public ResponseEntity<UserResponseDto> getById(
        @Parameter(description = "User id", example = "1") @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto user = service.registerUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> delete(
        @Parameter(description = "User id", example = "1") @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/change-user-information")
    @Operation(summary = "Update user information")
    public ResponseEntity<UserResponseDto> changePassword(
        @Parameter(description = "User id", example = "1") @PathVariable Long id,
        @Valid @RequestBody UserRequestDto dto) {
        return ResponseEntity.ok(service.patch(id, dto));
    }
}
