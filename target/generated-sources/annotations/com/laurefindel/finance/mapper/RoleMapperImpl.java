package com.laurefindel.finance.mapper;

import com.laurefindel.finance.dto.RoleDto;
import com.laurefindel.finance.model.entity.Role;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-23T01:57:17+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Arch Linux)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public RoleDto toRoleDto(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleDto roleDto = new RoleDto();

        roleDto.setName( role.getName() );

        return roleDto;
    }

    @Override
    public Role toRole(RoleDto roleDto) {
        if ( roleDto == null ) {
            return null;
        }

        Role role = new Role();

        role.setName( roleDto.getName() );

        return role;
    }
}
