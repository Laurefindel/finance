package com.laurefindel.finance.mapper;

import com.laurefindel.finance.model.entity.Account;
import com.laurefindel.finance.model.entity.Role;
import com.laurefindel.finance.model.entity.User;
import java.util.Collections;
import com.laurefindel.finance.dto.UserRequestDto;
import com.laurefindel.finance.dto.UserResponseDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(source = "accounts", target = "accountsIds", qualifiedByName = "mapAccountsToIds")
    @Mapping(source = "roles", target = "roleIds", qualifiedByName = "mapRolesToIds")
    public UserResponseDto toUserResponseDto(User user);

    @Named("mapAccountsToIds")
    default List<Long> mapAccountsToIds(List<Account> accounts) {
        if (accounts == null) return Collections.emptyList();
        return accounts.stream()
                       .map(Account::getId)
                       .toList();
    }
    
    @Named("mapRolesToIds")
    default Set<Long> mapRolesToIds(Set<Role> roles) {
        if (roles == null) return Collections.emptySet();
        return roles.stream()
                    .map(Role::getId)
                    .collect(Collectors.toSet());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "status", ignore = true)
    public User toUser(UserRequestDto userRequestDto);
}
