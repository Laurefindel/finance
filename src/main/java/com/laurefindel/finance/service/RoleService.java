package com.laurefindel.finance.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.laurefindel.finance.dto.RoleDto;
import com.laurefindel.finance.mapper.RoleMapper;
import com.laurefindel.finance.model.entity.Role;
import com.laurefindel.finance.model.entity.User;
import com.laurefindel.finance.repository.RoleRepository;

import jakarta.transaction.Transactional;

@Service
public class RoleService {
    private static final Logger LOG = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;
    private final RoleMapper mapper;

    public RoleService(RoleRepository roleRepository, RoleMapper mapper) {
        this.roleRepository = roleRepository;
        this.mapper = mapper;
    }

    public Optional<RoleDto> getById(Long id) {
        LOG.debug("Fetching role by id");
        return roleRepository.findById(id).map(mapper::toRoleDto);
    }

    public Optional<RoleDto> getByName(String name) {
        LOG.debug("Fetching role by name");
        return Optional.ofNullable(roleRepository.findByName(name)).map(mapper::toRoleDto);
    }

    public RoleDto save(RoleDto role) {
        LOG.info("Saving role");
        return mapper.toRoleDto(roleRepository.save(mapper.toRole(role)));
    }

    @Transactional
    public void delete(Long id) {
        LOG.info("Deleting role id={}", id);
        Role role = roleRepository.findById(id).orElseThrow();
        Set<User> users = new HashSet<>(role.getUsers());
        for (User user : users) {
            user.getRoles().remove(role);
        }
        roleRepository.delete(role);
    }

    public List<RoleDto> getAll() {
        List<RoleDto> roles = roleRepository.findAll()
                .stream()
                .map(mapper::toRoleDto)
                .toList();
        LOG.debug("Fetched all roles count={}", roles.size());
        return roles;
    }

    public List<RoleDto> getByIds(List<Long> ids) {
        List<RoleDto> roles = roleRepository.findAllById(ids)
                .stream()
                .map(mapper::toRoleDto)
                .toList();
        LOG.debug("Fetched {} roles by ids", roles.size());
        return roles;
    }

    public List<RoleDto> getByUsers(Set<User> users) {
        List<RoleDto> roles = roleRepository.findByUsers(users)
                .stream()
                .map(mapper::toRoleDto)
                .toList();
        LOG.debug("Fetched {} roles for users set size={}", roles.size(), users.size());
        return roles;
    }
    
    public Optional<Role> getEntityById(Long id) {
        LOG.debug("Fetching role entity by id");
        return roleRepository.findById(id);
    }

    public Optional<Role> getEntityByName(String name) {
        LOG.debug("Fetching role entity by name");
        return Optional.ofNullable(roleRepository.findByName(name));
    }
}
