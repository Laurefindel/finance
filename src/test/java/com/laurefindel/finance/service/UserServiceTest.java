package com.laurefindel.finance.service;

import com.laurefindel.finance.dto.UserRequestDto;
import com.laurefindel.finance.dto.UserResponseDto;
import com.laurefindel.finance.mapper.UserMapper;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.model.entity.Role;
import com.laurefindel.finance.model.entity.User;
import com.laurefindel.finance.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private RoleService roleService;

    @Mock
    private UserMapper mapper;

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private UserService service;

    @Test
    void save_shouldPersistMappedUser() {
        UserRequestDto request = new UserRequestDto();
        User entity = new User();
        UserResponseDto response = new UserResponseDto();

        when(mapper.toUser(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toUserResponseDto(entity)).thenReturn(response);

        UserResponseDto result = service.save(request);

        assertEquals(response, result);
    }

    @Test
    void registerUser_shouldCreateDefaultAccountAndRole() {
        UserRequestDto request = new UserRequestDto();
        User user = new User();
        user.setRoles(new java.util.HashSet<>());
        user.setAccounts(new ArrayList<>());

        Currency usd = new Currency();
        usd.setCode("USD");
        Role role = new Role();
        UserResponseDto response = new UserResponseDto();

        when(mapper.toUser(request)).thenReturn(user);
        when(currencyService.getEntityByCode("USD")).thenReturn(usd);
        when(roleService.getEntityByName("User")).thenReturn(role);
        when(repository.save(user)).thenReturn(user);
        when(mapper.toUserResponseDto(user)).thenReturn(response);

        UserResponseDto result = service.registerUser(request);

        assertEquals("ACTIVE", user.getStatus());
        assertEquals(1, user.getAccounts().size());
        assertEquals(usd, user.getAccounts().get(0).getCurrency());
        assertEquals(user, user.getAccounts().get(0).getUser());
        assertEquals(role, user.getRoles().iterator().next());
        assertEquals(response, result);
    }

    @Test
    void patch_shouldUpdateOnlyProvidedFields() {
        User existing = new User();
        existing.setFirstName("Old");
        existing.setLastName("Value");
        existing.setEmail("old@mail.com");
        existing.setPassword("old-pass");

        UserRequestDto patch = new UserRequestDto();
        patch.setFirstName("New");
        patch.setEmail("new@mail.com");

        UserResponseDto response = new UserResponseDto();

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toUserResponseDto(existing)).thenReturn(response);

        UserResponseDto result = service.patch(1L, patch);

        assertEquals("New", existing.getFirstName());
        assertEquals("Value", existing.getLastName());
        assertEquals("new@mail.com", existing.getEmail());
        assertEquals("old-pass", existing.getPassword());
        assertEquals(response, result);
        verify(repository).save(existing);
    }

    @Test
    void getAll_shouldMapAllUsers() {
        User user = new User();
        UserResponseDto response = new UserResponseDto();

        when(repository.findAll()).thenReturn(List.of(user));
        when(mapper.toUserResponseDto(user)).thenReturn(response);

        List<UserResponseDto> result = service.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getById_shouldReturnMappedUser() {
        User user = new User();
        UserResponseDto response = new UserResponseDto();

        when(repository.findById(2L)).thenReturn(Optional.of(user));
        when(mapper.toUserResponseDto(user)).thenReturn(response);

        UserResponseDto result = service.getById(2L);

        assertEquals(response, result);
    }

    @Test
    void getByFirstAndLastName_shouldReturnFirstMatch() {
        User user = new User();
        UserResponseDto response = new UserResponseDto();

        when(repository.findByFirstNameAndLastName("John", "Doe")).thenReturn(List.of(user));
        when(mapper.toUserResponseDto(user)).thenReturn(response);

        UserResponseDto result = service.getByFirstAndLastName("John", "Doe");

        assertEquals(response, result);
    }

    @Test
    void getByEmail_shouldReturnMappedUser() {
        User user = new User();
        UserResponseDto response = new UserResponseDto();

        when(repository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(mapper.toUserResponseDto(user)).thenReturn(response);

        UserResponseDto result = service.getByEmail("user@mail.com");

        assertEquals(response, result);
    }

    @Test
    void delete_shouldDelegateToRepository() {
        service.delete(9L);
        verify(repository).deleteById(9L);
    }

    @Test
    void patch_shouldUpdateAllFieldsWhenProvided() {
        User existing = new User();
        existing.setFirstName("Old");
        existing.setLastName("Name");
        existing.setEmail("old@mail.com");
        existing.setPassword("old-pass");

        UserRequestDto patch = new UserRequestDto();
        patch.setFirstName("New");
        patch.setLastName("Last");
        patch.setEmail("new@mail.com");
        patch.setPassword("new-pass");

        UserResponseDto response = new UserResponseDto();

        when(repository.findById(12L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toUserResponseDto(existing)).thenReturn(response);

        UserResponseDto result = service.patch(12L, patch);

        assertEquals("New", existing.getFirstName());
        assertEquals("Last", existing.getLastName());
        assertEquals("new@mail.com", existing.getEmail());
        assertEquals("new-pass", existing.getPassword());
        assertEquals(response, result);
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.getById(404L));
    }

    @Test
    void getByEmail_shouldThrowWhenNotFound() {
        when(repository.findByEmail("missing@mail.com")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.getByEmail("missing@mail.com"));
    }

    @Test
    void getByFirstAndLastName_shouldThrowWhenNotFound() {
        when(repository.findByFirstNameAndLastName("No", "User")).thenReturn(List.of());

        assertThrows(NoSuchElementException.class, () -> service.getByFirstAndLastName("No", "User"));
    }

    @Test
    void patch_shouldThrowWhenUserNotFound() {
        UserRequestDto patch = new UserRequestDto();
        when(repository.findById(505L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.patch(505L, patch));
    }
}
