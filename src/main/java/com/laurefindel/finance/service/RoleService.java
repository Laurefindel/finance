package com.laurefindel.finance.service;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.laurefindel.finance.dto.RoleDto;
import com.laurefindel.finance.mapper.RoleMapper;
import com.laurefindel.finance.model.entity.Role;
import com.laurefindel.finance.model.entity.User;
import com.laurefindel.finance.repository.RoleRepository;

@Service
public class RoleService {
    private static final Logger LOG = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;
    private final RoleMapper mapper;

    public RoleService(RoleRepository roleRepository, RoleMapper mapper) {
        this.roleRepository = roleRepository;
        this.mapper = mapper;
    }

    public RoleDto getById(Long id) {
        LOG.debug("Fetching role by id={}", id);
        return mapper.toRoleDto(roleRepository.findById(id).orElseThrow());
    }

    public RoleDto getByName(String name) {
        LOG.debug("Fetching role by name={}", name);
        return mapper.toRoleDto(roleRepository.findByName(name));
    }

    public RoleDto save(RoleDto role) {
        LOG.info("Saving role name={}", role.getName());
        return mapper.toRoleDto(roleRepository.save(mapper.toRole(role)));
    }

    public void delete(Long id) {
        LOG.info("Deleting role id={}", id);
        roleRepository.deleteById(id);
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
    
    public Role getEntityById(Long id) {
        LOG.debug("Fetching role entity by id={}", id);
        return roleRepository.findById(id).orElseThrow();
    }

    public Role getEntityByName(String name) {
        LOG.debug("Fetching role entity by name={}", name);
        return roleRepository.findByName(name);
    }
}