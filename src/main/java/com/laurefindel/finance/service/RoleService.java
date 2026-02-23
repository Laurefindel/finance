package com.laurefindel.finance.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.laurefindel.finance.dto.RoleDto;
import com.laurefindel.finance.mapper.RoleMapper;
import com.laurefindel.finance.model.entity.Role;
import com.laurefindel.finance.model.entity.User;
import com.laurefindel.finance.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper mapper;

    public RoleService(RoleRepository roleRepository, RoleMapper mapper) {
        this.roleRepository = roleRepository;
        this.mapper = mapper;
    }

    public RoleDto getById(Long id) {
        return mapper.toRoleDto(roleRepository.findById(id).orElseThrow());
    }

    public RoleDto getByName(String name) {
        return mapper.toRoleDto(roleRepository.findByName(name));
    }

    public RoleDto save(RoleDto role) {
        return mapper.toRoleDto(roleRepository.save(mapper.toRole(role)));
    }

    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    public List<RoleDto> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(mapper::toRoleDto)
                .toList();
    }

    public List<RoleDto> getByIds(List<Long> ids) {
        return roleRepository.findAllById(ids)
                .stream()
                .map(mapper::toRoleDto)
                .toList();
    }

    public List<RoleDto> getByUsers(Set<User> users) {
        return roleRepository.findByUsers(users)
                .stream()
                .map(mapper::toRoleDto)
                .toList();
    }
    
    public Role getEntityById(Long id) {
        return roleRepository.findById(id).orElseThrow();
    }

    public Role getEntityByName(String name) {
        return roleRepository.findByName(name);
    }
}