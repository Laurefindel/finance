package com.laurefindel.finance.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.laurefindel.finance.dto.AccountRequestDto;
import com.laurefindel.finance.dto.AccountResponseDto;
import com.laurefindel.finance.mapper.AccountMapper;
import com.laurefindel.finance.model.entity.Account;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.repository.AccountRepository;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper mapper;

    public AccountService(AccountRepository accountRepository, AccountMapper mapper) {
        this.accountRepository = accountRepository;
        this.mapper = mapper;
    }
    public AccountResponseDto getById(Long id) {
        return mapper.toAccountResponseDto(accountRepository
            .findById(id)
            .orElseThrow());
    }
    public AccountResponseDto save(AccountRequestDto account) {
        return mapper.toAccountResponseDto(accountRepository
            .save(mapper
                .toAccount(account)));
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
}
