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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.laurefindel.finance.dto.RoleDto;
import com.laurefindel.finance.service.RoleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/roles")
@Validated
@Tag(name = "Roles", description = "Role management endpoints")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @Operation(summary = "Get all roles")
    public ResponseEntity<List<RoleDto>> getAll() {
        return ResponseEntity.ok(roleService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by id")
    public ResponseEntity<RoleDto> getById(
        @Parameter(description = "Role id", example = "1") @PathVariable Long id
    ) {
        return ResponseEntity.ok(roleService.getById(id).orElseThrow());
    }

    @GetMapping("/by-name")
    @Operation(summary = "Get role by name")
    public ResponseEntity<RoleDto> getByName(
        @Parameter(description = "Role name", example = "User") @RequestParam String name
    ) {
        return ResponseEntity.ok(roleService.getByName(name).orElseThrow());
    }

    @PostMapping
    @Operation(summary = "Create role")
    public ResponseEntity<RoleDto> create(@Valid @RequestBody RoleDto dto) {
        RoleDto savedRole = roleService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRole);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role")
    public ResponseEntity<Void> delete(
        @Parameter(description = "Role id", example = "1") @PathVariable Long id
    ) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
