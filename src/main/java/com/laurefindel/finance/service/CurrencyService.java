package com.laurefindel.finance.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.laurefindel.finance.dto.CurrencyRequestDto;
import com.laurefindel.finance.dto.CurrencyResponseDto;
import com.laurefindel.finance.mapper.CurrencyMapper;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.repository.CurrencyRepository;

@Service
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper mapper;

    public CurrencyService(CurrencyRepository currencyRepository, CurrencyMapper mapper) {
        this.currencyRepository = currencyRepository;
        this.mapper = mapper;
    }

    public CurrencyResponseDto getById(Long id) {
        return mapper.toCurrencyResponseDto(currencyRepository.findById(id).orElseThrow()); 
    }

    public CurrencyResponseDto getByCode(String code) {
        return mapper.toCurrencyResponseDto(currencyRepository.findByCode(code));
    }

    public CurrencyResponseDto save(Currency currency) {
        return mapper.toCurrencyResponseDto(currencyRepository.save(currency));
    }

    public void delete(Long id) {
        currencyRepository.deleteById(id);
    }

    public List<CurrencyResponseDto> getByName(String name) {
        return currencyRepository.findByName(name)
                .stream()
                .map(mapper::toCurrencyResponseDto)
                .toList();
    }
    
    public List<CurrencyResponseDto> getAll() {
        return currencyRepository.findAll()
                .stream()
                .map(mapper::toCurrencyResponseDto)
                .toList();
    }

    public Currency getEntityByCode(String code) {
        return currencyRepository.findByCode(code);
    }

    public CurrencyResponseDto update(Long id, CurrencyRequestDto dto) {
        Currency currency = currencyRepository.findById(id).orElseThrow();
        currency.setCode(dto.getCode());
        currency.setName(dto.getName());
        return mapper.toCurrencyResponseDto(currencyRepository.save(currency));
    }

    public CurrencyResponseDto patch(Long id, CurrencyRequestDto dto) {
        Currency currency = currencyRepository.findById(id).orElseThrow();

        if (dto.getCode() != null) {
            currency.setCode(dto.getCode());
        }

        if (dto.getName() != null) {
            currency.setName(dto.getName());
        }

        return mapper.toCurrencyResponseDto(currencyRepository.save(currency));
    }
}
