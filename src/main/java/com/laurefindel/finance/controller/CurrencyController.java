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

import com.laurefindel.finance.dto.CurrencyResponseDto;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.service.CurrencyService;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping
    public List<CurrencyResponseDto> getAll() {
        return currencyService.getAll();
    }

    @GetMapping("/{id}")
    public CurrencyResponseDto getById(@PathVariable Long id) {
        return currencyService.getById(id);
    }

    @GetMapping("/by-code")
    public CurrencyResponseDto getByCode(@RequestParam String code) {
        return currencyService.getByCode(code);
    }

    @GetMapping("/by-name")
    public List<CurrencyResponseDto> getByName(@RequestParam String name) {
        return currencyService.getByName(name);
    }

    @PostMapping
    public CurrencyResponseDto create(@RequestBody Currency currency) {
        return currencyService.save(currency);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        currencyService.delete(id);
    }
}