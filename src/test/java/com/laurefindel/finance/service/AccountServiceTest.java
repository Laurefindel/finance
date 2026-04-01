package com.laurefindel.finance.service;

import com.laurefindel.finance.dto.AccountRequestDto;
import com.laurefindel.finance.dto.AccountResponseDto;
import com.laurefindel.finance.mapper.AccountMapper;
import com.laurefindel.finance.model.entity.Account;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.model.entity.User;
import com.laurefindel.finance.repository.AccountRepository;
import com.laurefindel.finance.repository.CurrencyRepository;
import com.laurefindel.finance.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper mapper;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService service;

    @Test
    void save_shouldCreateAccountWithDefaultFields() {
        AccountRequestDto request = new AccountRequestDto();
        request.setUserId(10L);
        request.setCurrencyId(1L);

        User user = new User();
        user.setId(10L);
        user.setAccounts(new ArrayList<>());

        Currency currency = new Currency();
        currency.setId(1L);

        Account mapped = new Account();
        AccountResponseDto response = new AccountResponseDto();

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(currencyRepository.findById(1L)).thenReturn(Optional.of(currency));
        when(mapper.toAccount(request)).thenReturn(mapped);
        when(mapper.toAccountResponseDto(mapped)).thenReturn(response);

        AccountResponseDto result = service.save(request);

        assertEquals(response, result);
        assertEquals("ACTIVE", mapped.getStatus());
        assertEquals(BigDecimal.ZERO, mapped.getBalance());
        assertEquals(user, mapped.getUser());
        assertEquals(currency, mapped.getCurrency());
        verify(userRepository).save(user);
    }

    @Test
    void replenish_shouldIncreaseBalanceAndSave() {
        Account account = new Account();
        account.setBalance(new BigDecimal("50.00"));

        AccountResponseDto response = new AccountResponseDto();

        when(accountRepository.findById(7L)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);
        when(mapper.toAccountResponseDto(account)).thenReturn(response);

        AccountResponseDto result = service.replenish(7L, new BigDecimal("10.00"));

        assertEquals(new BigDecimal("60.00"), account.getBalance());
        assertEquals(response, result);
    }

    @Test
    void getAll_shouldMapAllAccounts() {
        Account account = new Account();
        AccountResponseDto response = new AccountResponseDto();

        when(accountRepository.findAll()).thenReturn(List.of(account));
        when(mapper.toAccountResponseDto(account)).thenReturn(response);

        List<AccountResponseDto> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void saveEntity_shouldDelegateToRepository() {
        Account account = new Account();
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account result = service.save(account);

        assertEquals(account, result);
        verify(accountRepository).save(account);
    }

    @Test
    void getById_shouldReturnMappedAccount() {
        Account account = new Account();
        AccountResponseDto response = new AccountResponseDto();

        when(accountRepository.findById(2L)).thenReturn(Optional.of(account));
        when(mapper.toAccountResponseDto(account)).thenReturn(response);

        AccountResponseDto result = service.getById(2L).orElseThrow();

        assertEquals(response, result);
    }

    @Test
    void delete_shouldDelegateToRepository() {
        service.delete(3L);
        verify(accountRepository).deleteById(3L);
    }

    @Test
    void getByUserId_shouldMapFilteredAccounts() {
        Account account = new Account();
        AccountResponseDto response = new AccountResponseDto();

        when(accountRepository.findByUserId(11L)).thenReturn(List.of(account));
        when(mapper.toAccountResponseDto(account)).thenReturn(response);

        List<AccountResponseDto> result = service.getByUserId(11L);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getByCurrency_shouldMapFilteredAccounts() {
        Currency currency = new Currency();
        Account account = new Account();
        AccountResponseDto response = new AccountResponseDto();

        when(accountRepository.findByCurrency(currency)).thenReturn(List.of(account));
        when(mapper.toAccountResponseDto(account)).thenReturn(response);

        List<AccountResponseDto> result = service.getByCurrency(currency);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getByUserIdAndCurrency_shouldMapFilteredAccounts() {
        Currency currency = new Currency();
        Account account = new Account();
        AccountResponseDto response = new AccountResponseDto();

        when(accountRepository.findByUserIdAndCurrency(5L, currency)).thenReturn(List.of(account));
        when(mapper.toAccountResponseDto(account)).thenReturn(response);

        List<AccountResponseDto> result = service.getByUserIdAndCurrency(5L, currency);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getEntityById_shouldReturnEntity() {
        Account account = new Account();
        when(accountRepository.findById(9L)).thenReturn(Optional.of(account));

        Account result = service.getEntityById(9L).orElseThrow();

        assertEquals(account, result);
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(accountRepository.findById(404L)).thenReturn(Optional.empty());

        assertTrue(service.getById(404L).isEmpty());
    }

    @Test
    void getEntityById_shouldThrowWhenNotFound() {
        when(accountRepository.findById(505L)).thenReturn(Optional.empty());

        assertTrue(service.getEntityById(505L).isEmpty());
    }

    @Test
    void save_shouldThrowWhenUserNotFound() {
        AccountRequestDto request = new AccountRequestDto();
        request.setUserId(99L);
        request.setCurrencyId(1L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.save(request));
    }

    @Test
    void save_shouldThrowWhenCurrencyNotFound() {
        AccountRequestDto request = new AccountRequestDto();
        request.setUserId(10L);
        request.setCurrencyId(77L);

        User user = new User();
        user.setAccounts(new ArrayList<>());

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(currencyRepository.findById(77L)).thenReturn(Optional.empty());
        when(mapper.toAccount(request)).thenReturn(new Account());

        assertThrows(NoSuchElementException.class, () -> service.save(request));
    }

    @Test
    void replenish_shouldThrowWhenAccountNotFound() {
        when(accountRepository.findById(606L)).thenReturn(Optional.empty());
        BigDecimal amount = new BigDecimal("5.00");

        assertThrows(NoSuchElementException.class, () -> service.replenish(606L, amount));
    }
}
