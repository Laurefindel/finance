package com.laurefindel.finance.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

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
        LOG.debug("Fetching user by id={}", id);
        return mapper
            .toUserResponseDto(repository.findById(id).orElseThrow());
    }

    public List<UserResponseDto> getAll() {
        List<UserResponseDto> users = repository.findAll().stream()
                .map(mapper::toUserResponseDto).toList();
        LOG.debug("Fetched all users count={}", users.size());
        return users;
    }

    public UserResponseDto getByFirstAndLastName(String firstName, String lastName) {
        LOG.debug("Fetching user by firstName={} and lastName={}", firstName, lastName);
        return mapper
            .toUserResponseDto(repository.findByFirstNameAndLastName(firstName, lastName)
            .stream()
            .findFirst()
            .orElseThrow());
    }

    public UserResponseDto getByEmail(String email) {
        LOG.debug("Fetching user by email={}", email);
        return mapper
            .toUserResponseDto(repository.findByEmail(email).orElseThrow());
    }

    public UserResponseDto save(UserRequestDto user) {
        LOG.info("Creating user with email={}", user.getEmail());
        return mapper
            .toUserResponseDto(repository.save(mapper.toUser(user)));
    }

    public void delete(Long id) {
        LOG.info("Deleting user id={}", id);
        repository.deleteById(id);
    }

    @Transactional
    public UserResponseDto registerUser(UserRequestDto userDto) {
        LOG.info("Registering user with email={}", userDto.getEmail());
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
        LOG.info("User registered id={} with default account and role", savedUser.getId());

        return mapper.toUserResponseDto(savedUser);
    }

    public UserResponseDto patch(Long id, UserRequestDto dto) {
        LOG.info("Patching user id={}", id);
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
        
        User savedUser = repository.save(user);
        LOG.info("User patched id={}", savedUser.getId());
        return mapper.toUserResponseDto(savedUser);
    }
}
