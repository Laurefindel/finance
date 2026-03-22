package com.laurefindel.finance.controller;

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

import com.laurefindel.finance.dto.CurrencyRequestDto;
import com.laurefindel.finance.dto.CurrencyResponseDto;
import com.laurefindel.finance.service.CurrencyService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/currencies")
@Validated
@Tag(name = "Currencies", description = "Currency management endpoints")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping
    @Operation(summary = "Get all currencies")
    public ResponseEntity<List<CurrencyResponseDto>> getAll() {
        return ResponseEntity.ok(currencyService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get currency by id")
    public ResponseEntity<CurrencyResponseDto> getById(
        @Parameter(description = "Currency id", example = "1") @PathVariable Long id
    ) {
        return ResponseEntity.ok(currencyService.getById(id));
    }

    @GetMapping("/by-code")
    @Operation(summary = "Get currency by code")
    public ResponseEntity<CurrencyResponseDto> getByCode(
        @Parameter(description = "Currency code", example = "USD") @RequestParam String code
    ) {
        return ResponseEntity.ok(currencyService.getByCode(code));
    }

    @GetMapping("/by-name")
    @Operation(summary = "Get currencies by name")
    public ResponseEntity<List<CurrencyResponseDto>> getByName(
        @Parameter(description = "Currency name", example = "US Dollar") @RequestParam String name
    ) {
        return ResponseEntity.ok(currencyService.getByName(name));
    }

    @PostMapping
    @Operation(summary = "Create currency")
    public ResponseEntity<CurrencyResponseDto> create(@Valid @RequestBody CurrencyRequestDto dto) {
        CurrencyResponseDto currency = currencyService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(currency);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete currency")
    public ResponseEntity<Void> delete(
        @Parameter(description = "Currency id", example = "1") @PathVariable Long id
    ) {
        currencyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace currency")
    public ResponseEntity<CurrencyResponseDto> putCurrency(
        @Parameter(description = "Currency id", example = "1") @PathVariable Long id,
        @Valid @RequestBody CurrencyRequestDto dto) {
        return ResponseEntity.ok(currencyService.update(id, dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update currency partially")
    public ResponseEntity<CurrencyResponseDto> patchCurrency(
        @Parameter(description = "Currency id", example = "1") @PathVariable Long id,
        @Valid @RequestBody CurrencyRequestDto dto) {
        return ResponseEntity.ok(currencyService.patch(id, dto));
    }
}