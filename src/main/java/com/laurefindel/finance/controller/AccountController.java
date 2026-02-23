package com.laurefindel.finance.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.laurefindel.finance.dto.AccountRequestDto;
import com.laurefindel.finance.dto.AccountResponseDto;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.service.AccountService;
import com.laurefindel.finance.service.CurrencyService;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final CurrencyService currencyService;

    public AccountController(AccountService accountService,
                             CurrencyService currencyService) {
        this.accountService = accountService;
        this.currencyService = currencyService;
    }

    @GetMapping("/{id}")
    public AccountResponseDto getById(@PathVariable Long id) {
        return accountService.getById(id);
    }

    @GetMapping
    public List<AccountResponseDto> getAccounts(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String currency) {

        if (userId != null && currency != null) {
            Currency curr = currencyService.getEntityByCode(currency);
            return accountService.getByUserIdAndCurrency(userId, curr);
        }

        if (userId != null) {
            return accountService.getByUserId(userId);
        }

        if (currency != null) {
            Currency curr = currencyService.getEntityByCode(currency);
            return accountService.getByCurrency(curr);
        }

        return accountService.getAll();
    }

    @PostMapping
    public AccountResponseDto create(@RequestBody AccountRequestDto dto) {
        return accountService.save(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        accountService.delete(id);
    }
}