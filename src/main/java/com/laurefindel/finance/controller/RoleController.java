package com.laurefindel.finance.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.laurefindel.finance.dto.RoleDto;
import com.laurefindel.finance.service.RoleService;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleDto> getAll() {
        return roleService.getAll();
    }

    @GetMapping("/{id}")
    public RoleDto getById(@PathVariable Long id) {
        return roleService.getById(id);
    }

    @GetMapping("/by-name")
    public RoleDto getByName(@RequestParam String name) {
        return roleService.getByName(name);
    }

    @PostMapping
    public RoleDto create(@RequestBody RoleDto dto) {
        return roleService.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        roleService.delete(id);
    }
}