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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
