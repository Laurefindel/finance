package com.laurefindel.finance.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.laurefindel.finance.dto.CurrencyRequestDto;
import com.laurefindel.finance.dto.CurrencyResponseDto;
import com.laurefindel.finance.mapper.CurrencyMapper;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.repository.CurrencyRepository;

@Service
public class CurrencyService {
    private static final Logger LOG = LoggerFactory.getLogger(CurrencyService.class);

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper mapper;

    public CurrencyService(CurrencyRepository currencyRepository, CurrencyMapper mapper) {
        this.currencyRepository = currencyRepository;
        this.mapper = mapper;
    }

    public CurrencyResponseDto getById(Long id) {
        LOG.debug("Fetching currency by id={}", id);
        return mapper.toCurrencyResponseDto(currencyRepository.findById(id).orElseThrow()); 
    }

    public CurrencyResponseDto getByCode(String code) {
        LOG.debug("Fetching currency by code={}", code);
        return mapper.toCurrencyResponseDto(currencyRepository.findByCode(code));
    }

    public CurrencyResponseDto save(CurrencyRequestDto currency) {
        LOG.info("Creating currency code={} name={}", currency.getCode(), currency.getName());
        return mapper.toCurrencyResponseDto(currencyRepository.save(mapper.toCurrency(currency)));
    }

    public void delete(Long id) {
        LOG.info("Deleting currency id={}", id);
        currencyRepository.deleteById(id);
    }

    public List<CurrencyResponseDto> getByName(String name) {
        List<CurrencyResponseDto> currencies = currencyRepository.findByName(name)
                .stream()
                .map(mapper::toCurrencyResponseDto)
                .toList();
        LOG.debug("Fetched {} currencies by name={}", currencies.size(), name);
        return currencies;
    }
    
    public List<CurrencyResponseDto> getAll() {
        List<CurrencyResponseDto> currencies = currencyRepository.findAll()
                .stream()
                .map(mapper::toCurrencyResponseDto)
                .toList();
        LOG.debug("Fetched all currencies count={}", currencies.size());
        return currencies;
    }

    public Currency getEntityByCode(String code) {
        LOG.debug("Fetching currency entity by code={}", code);
        return currencyRepository.findByCode(code);
    }

    public CurrencyResponseDto update(Long id, CurrencyRequestDto dto) {
        LOG.info("Updating currency id={}", id);
        Currency currency = currencyRepository.findById(id).orElseThrow();
        currency.setCode(dto.getCode());
        currency.setName(dto.getName());
        Currency savedCurrency = currencyRepository.save(currency);
        LOG.info("Currency updated id={} code={}", savedCurrency.getId(), savedCurrency.getCode());
        return mapper.toCurrencyResponseDto(savedCurrency);
    }

    public CurrencyResponseDto patch(Long id, CurrencyRequestDto dto) {
        LOG.info("Patching currency id={}", id);
        Currency currency = currencyRepository.findById(id).orElseThrow();

        if (dto.getCode() != null) {
            currency.setCode(dto.getCode());
        }

        if (dto.getName() != null) {
            currency.setName(dto.getName());
        }

        Currency savedCurrency = currencyRepository.save(currency);
        LOG.info("Currency patched id={} code={}", savedCurrency.getId(), savedCurrency.getCode());
        return mapper.toCurrencyResponseDto(savedCurrency);
    }
}
