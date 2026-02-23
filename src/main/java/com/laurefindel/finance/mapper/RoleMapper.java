package com.laurefindel.finance.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.laurefindel.finance.dto.RoleDto;
import com.laurefindel.finance.model.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    public RoleDto toRoleDto(Role role);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    public Role toRole(RoleDto roleDto);
    
}
