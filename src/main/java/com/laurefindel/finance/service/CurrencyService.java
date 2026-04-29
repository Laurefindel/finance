package com.laurefindel.finance.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.laurefindel.finance.dto.CurrencyRequestDto;
import com.laurefindel.finance.dto.CurrencyResponseDto;
import com.laurefindel.finance.mapper.CurrencyMapper;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.repository.CurrencyRepository;

import jakarta.transaction.Transactional;

@Service
public class CurrencyService {
    private static final Logger LOG = LoggerFactory.getLogger(CurrencyService.class);

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper mapper;
    private final FinancialOperationSearchCache operationSearchCache;

    public CurrencyService(CurrencyRepository currencyRepository, CurrencyMapper mapper,
         FinancialOperationSearchCache operationSearchCache) {
        this.currencyRepository = currencyRepository;
        this.mapper = mapper;
        this.operationSearchCache = operationSearchCache;
    }

    public Optional<CurrencyResponseDto> getById(Long id) {
        LOG.debug("Fetching currency by id");
        return currencyRepository.findById(id).map(mapper::toCurrencyResponseDto);
    }

    public Optional<CurrencyResponseDto> getByCode(String code) {
        LOG.debug("Fetching currency by code");
        return Optional.ofNullable(currencyRepository.findByCode(code))
            .map(mapper::toCurrencyResponseDto);
    }

    public CurrencyResponseDto save(CurrencyRequestDto currency) {
        LOG.info("Creating currency");
        return mapper.toCurrencyResponseDto(currencyRepository.save(mapper.toCurrency(currency)));
    }

    @Transactional
    public void delete(Long id) {
        LOG.info("Deleting currency id={}", id);
        Currency currency = currencyRepository.findById(id).orElseThrow();
        currencyRepository.delete(currency);
        operationSearchCache.invalidate();
    }

    public List<CurrencyResponseDto> getByName(String name) {
        List<CurrencyResponseDto> currencies = currencyRepository.findByName(name)
                .stream()
                .map(mapper::toCurrencyResponseDto)
                .toList();
        LOG.debug("Fetched currencies by name, count={}", currencies.size());
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

    public Optional<Currency> getEntityByCode(String code) {
        LOG.debug("Fetching currency entity by code");
        return Optional.ofNullable(currencyRepository.findByCode(code));
    }

    public CurrencyResponseDto update(Long id, CurrencyRequestDto dto) {
        LOG.info("Updating currency");
        Currency currency = currencyRepository.findById(id).orElseThrow();
        currency.setCode(dto.getCode());
        currency.setName(dto.getName());
        Currency savedCurrency = currencyRepository.save(currency);
        LOG.info("Currency updated id={}", savedCurrency.getId());
        return mapper.toCurrencyResponseDto(savedCurrency);
    }

    public CurrencyResponseDto patch(Long id, CurrencyRequestDto dto) {
        LOG.info("Patching currency");
        Currency currency = currencyRepository.findById(id).orElseThrow();

        if (dto.getCode() != null) {
            currency.setCode(dto.getCode());
        }

        if (dto.getName() != null) {
            currency.setName(dto.getName());
        }

        Currency savedCurrency = currencyRepository.save(currency);
        LOG.info("Currency patched id={}", savedCurrency.getId());
        return mapper.toCurrencyResponseDto(savedCurrency);
    }
}
