package com.laurefindel.finance.service;

import org.springframework.stereotype.Service;

import com.laurefindel.finance.dto.UserRequestDto;
import com.laurefindel.finance.dto.UserResponseDto;
import com.laurefindel.finance.mapper.UserMapper;
import com.laurefindel.finance.model.entity.Account;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.model.entity.User;
import com.laurefindel.finance.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;
    private final RoleService roleService;
    private final UserMapper mapper;
    private final CurrencyService currencyService;

    public UserService(UserRepository repository, RoleService roleService,
         UserMapper mapper, CurrencyService currencyService) {
        this.repository = repository;
        this.roleService = roleService;
        this.mapper = mapper;
        this.currencyService = currencyService;
    }

    public UserResponseDto getById(Long id) {
        return mapper
        .toUserResponseDto(repository.findById(id).orElseThrow());
    }

    public List<UserResponseDto> getAll() {
        return repository
        .findAll()
        .stream()
        .map(mapper::toUserResponseDto)
        .toList();
    }

    public UserResponseDto getByFirstAndLastName(String firstName, String lastName) {
        return mapper
        .toUserResponseDto(repository.findByFirstNameAndLastName(firstName, lastName)
        .stream()
        .findFirst()
        .orElseThrow());
    }

    public UserResponseDto getByEmail(String email) {
        return mapper
        .toUserResponseDto(repository.findByEmail(email).orElseThrow());
    }

    public UserResponseDto save(UserRequestDto user) {
        return mapper
        .toUserResponseDto(repository.save(mapper.toUser(user)));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public UserResponseDto registerUser(UserRequestDto userDto) {
        User user = mapper.toUser(userDto);
        user.setStatus("ACTIVE");

        Currency defaultCurrency = currencyService.getEntityByCode("USD");
        Account account = new Account();
        account.setBalance(BigDecimal.ZERO);
        account.setStatus("ACTIVE");
        account.setCurrency(defaultCurrency);
        account.setUser(user);
        List<Account> accounts = new ArrayList<>();
        accounts.add(account);
        user.setAccounts(accounts);
        user.getRoles().add(roleService.getEntityByName("User"));

        User savedUser = repository.save(user);

        return mapper.toUserResponseDto(savedUser);
    }

    public UserResponseDto patch(Long id, UserRequestDto dto) {
        User user = repository.findById(id).orElseThrow();

        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null) {
            user.setPassword(dto.getPassword());
        }
        
        return mapper.toUserResponseDto(repository.save(user));
    }
}
