package com.laurefindel.finance.mapper;

import com.laurefindel.finance.dto.UserRequestDto;
import com.laurefindel.finance.dto.UserResponseDto;
import com.laurefindel.finance.model.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-23T01:57:17+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Arch Linux)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDto toUserResponseDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setAccountsIds( mapAccountsToIds( user.getAccounts() ) );
        userResponseDto.setRoleIds( mapRolesToIds( user.getRoles() ) );
        userResponseDto.setId( user.getId() );
        userResponseDto.setFirstName( user.getFirstName() );
        userResponseDto.setLastName( user.getLastName() );
        userResponseDto.setEmail( user.getEmail() );
        userResponseDto.setStatus( user.getStatus() );

        return userResponseDto;
    }

    @Override
    public User toUser(UserRequestDto userRequestDto) {
        if ( userRequestDto == null ) {
            return null;
        }

        User user = new User();

        user.setFirstName( userRequestDto.getFirstName() );
        user.setLastName( userRequestDto.getLastName() );
        user.setEmail( userRequestDto.getEmail() );
        user.setPassword( userRequestDto.getPassword() );

        return user;
    }
}
