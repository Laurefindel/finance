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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
