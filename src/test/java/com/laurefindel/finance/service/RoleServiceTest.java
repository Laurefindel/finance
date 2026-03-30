package com.laurefindel.finance.service;

import com.laurefindel.finance.dto.RoleDto;
import com.laurefindel.finance.mapper.RoleMapper;
import com.laurefindel.finance.model.entity.Role;
import com.laurefindel.finance.model.entity.User;
import com.laurefindel.finance.repository.RoleRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper mapper;

    @InjectMocks
    private RoleService service;

    @Test
    void save_shouldMapAndPersistRole() {
        RoleDto request = new RoleDto();
        Role entity = new Role();
        RoleDto response = new RoleDto();

        when(mapper.toRole(request)).thenReturn(entity);
        when(roleRepository.save(entity)).thenReturn(entity);
        when(mapper.toRoleDto(entity)).thenReturn(response);

        RoleDto result = service.save(request);

        assertEquals(response, result);
    }

    @Test
    void getByIds_shouldReturnMappedRoles() {
        Role role = new Role();
        RoleDto roleDto = new RoleDto();

        when(roleRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(role));
        when(mapper.toRoleDto(role)).thenReturn(roleDto);

        List<RoleDto> result = service.getByIds(List.of(1L, 2L));

        assertEquals(1, result.size());
        assertEquals(roleDto, result.get(0));
    }

    @Test
    void delete_shouldDelegateToRepository() {
        service.delete(4L);
        verify(roleRepository).deleteById(4L);
    }

    @Test
    void getByUsers_shouldMapRolesByUsers() {
        User user = new User();
        Role role = new Role();
        RoleDto roleDto = new RoleDto();

        when(roleRepository.findByUsers(anySet())).thenReturn(List.of(role));
        when(mapper.toRoleDto(role)).thenReturn(roleDto);

        List<RoleDto> result = service.getByUsers(Set.of(user));

        assertEquals(1, result.size());
        assertEquals(roleDto, result.get(0));
    }

    @Test
    void getById_shouldReturnMappedRole() {
        Role role = new Role();
        RoleDto roleDto = new RoleDto();

        when(roleRepository.findById(1L)).thenReturn(java.util.Optional.of(role));
        when(mapper.toRoleDto(role)).thenReturn(roleDto);

        RoleDto result = service.getById(1L);

        assertEquals(roleDto, result);
    }

    @Test
    void getByName_shouldReturnMappedRole() {
        Role role = new Role();
        RoleDto roleDto = new RoleDto();

        when(roleRepository.findByName("User")).thenReturn(role);
        when(mapper.toRoleDto(role)).thenReturn(roleDto);

        RoleDto result = service.getByName("User");

        assertEquals(roleDto, result);
    }

    @Test
    void getAll_shouldReturnMappedRoles() {
        Role role = new Role();
        RoleDto roleDto = new RoleDto();

        when(roleRepository.findAll()).thenReturn(List.of(role));
        when(mapper.toRoleDto(role)).thenReturn(roleDto);

        List<RoleDto> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals(roleDto, result.get(0));
    }

    @Test
    void getEntityById_shouldReturnEntity() {
        Role role = new Role();
        when(roleRepository.findById(3L)).thenReturn(java.util.Optional.of(role));

        Role result = service.getEntityById(3L);

        assertEquals(role, result);
    }

    @Test
    void getEntityByName_shouldReturnEntity() {
        Role role = new Role();
        when(roleRepository.findByName("Admin")).thenReturn(role);

        Role result = service.getEntityByName("Admin");

        assertEquals(role, result);
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(roleRepository.findById(707L)).thenReturn(java.util.Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.getById(707L));
    }

    @Test
    void getEntityById_shouldThrowWhenNotFound() {
        when(roleRepository.findById(808L)).thenReturn(java.util.Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.getEntityById(808L));
    }
}
