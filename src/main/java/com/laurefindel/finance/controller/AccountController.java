package com.laurefindel.finance.controller;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/accounts")
@Validated
@Tag(name = "Accounts", description = "Operations with user accounts")
public class AccountController {

    private final AccountService accountService;
    private final CurrencyService currencyService;

    public AccountController(AccountService accountService,
                             CurrencyService currencyService) {
        this.accountService = accountService;
        this.currencyService = currencyService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by id")
    public ResponseEntity<AccountResponseDto> getById(
            @Parameter(description = "Account id", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(accountService.getById(id).orElseThrow());
    }

    @GetMapping
    @Operation(summary = "Get accounts with optional filters by user or currency")
    public ResponseEntity<List<AccountResponseDto>> getAccounts(
            @Parameter(description = "User id", example = "1")
            @RequestParam(required = false) Long userId,
            @Parameter(description = "Currency code", example = "USD")
            @RequestParam(required = false) String currency) {

        if (userId != null && currency != null) {
            Currency curr = currencyService.getEntityByCode(currency).orElseThrow();
            return ResponseEntity.ok(accountService.getByUserIdAndCurrency(userId, curr));
        }

        if (userId != null) {
            return ResponseEntity.ok(accountService.getByUserId(userId));
        }

        if (currency != null) {
            Currency curr = currencyService.getEntityByCode(currency).orElseThrow();
            return ResponseEntity.ok(accountService.getByCurrency(curr));
        }

        return ResponseEntity.ok(accountService.getAll());
    }

    @PostMapping
    @Operation(summary = "Create account")
    public ResponseEntity<AccountResponseDto> create(@Valid @RequestBody AccountRequestDto dto) {
        AccountResponseDto account = accountService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete account")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Account id", example = "1") @PathVariable Long id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/replenish/{amount}")
    @Operation(summary = "Replenish account balance")
    public ResponseEntity<AccountResponseDto> replenish(
            @Parameter(description = "Account id", example = "1") @PathVariable Long id,
            @Parameter(description = "Replenish amount", example = "100.00") @PathVariable BigDecimal amount) {
        return ResponseEntity.ok(accountService.replenish(id, amount));
    }
}