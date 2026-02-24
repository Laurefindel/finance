package com.laurefindel.finance.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.laurefindel.finance.dto.AccountRequestDto;
import com.laurefindel.finance.dto.AccountResponseDto;
import com.laurefindel.finance.mapper.AccountMapper;
import com.laurefindel.finance.model.entity.Account;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.model.entity.User;
import com.laurefindel.finance.repository.AccountRepository;
import com.laurefindel.finance.repository.CurrencyRepository;
import com.laurefindel.finance.repository.UserRepository;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper mapper;
    private final CurrencyRepository currencyRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, AccountMapper mapper,
         CurrencyRepository currencyRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.mapper = mapper;
        this.currencyRepository = currencyRepository;
        this.userRepository = userRepository;
    }

    public AccountResponseDto getById(Long id) {
        return mapper.toAccountResponseDto(accountRepository
            .findById(id)
            .orElseThrow());
    }

    public AccountResponseDto save(AccountRequestDto account) {
        User user = userRepository.findById(account.getUserId()).orElseThrow();
        Account accountEntity = mapper.toAccount(account);
        Currency currency = currencyRepository.findById(account.getCurrencyId()).orElseThrow();
        accountEntity.setCurrency(currency);
        accountEntity.setUser(user);
        accountEntity.setStatus("ACTIVE");
        accountEntity.setBalance(BigDecimal.ZERO);
        user.getAccounts().add(accountEntity);
        userRepository.save(user);
        return mapper.toAccountResponseDto(accountEntity);
    }

    public void delete(Long id) {
        accountRepository.deleteById(id);
    }

    public List<AccountResponseDto> getByUserId(Long userId) {
        return accountRepository.findByUserId(userId)
        .stream()
        .map(mapper::toAccountResponseDto)
        .toList();
    }

    public List<AccountResponseDto> getByCurrency(Currency currency) {
        return accountRepository.findByCurrency(currency)
        .stream()
        .map(mapper::toAccountResponseDto)
        .toList();
    }

    public List<AccountResponseDto> getByUserIdAndCurrency(Long userId, Currency currency) {
        return accountRepository.findByUserIdAndCurrency(userId, currency)
        .stream()
        .map(mapper::toAccountResponseDto)
        .toList();
    }

    public List<AccountResponseDto> getAll() {
        return accountRepository.findAll()
        .stream()
        .map(mapper::toAccountResponseDto)
        .toList();
    }

    public Account getEntityById(Long id) {
        return accountRepository.findById(id).orElseThrow();
    }

    public AccountResponseDto replenish(Long id, BigDecimal amount) {
        Account account = accountRepository.findById(id).orElseThrow();
        account.setBalance(account.getBalance().add(amount));
        return mapper.toAccountResponseDto(accountRepository.save(account));
    }
}
