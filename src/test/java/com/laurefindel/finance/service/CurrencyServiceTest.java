package com.laurefindel.finance.service;

import com.laurefindel.finance.dto.CurrencyRequestDto;
import com.laurefindel.finance.dto.CurrencyResponseDto;
import com.laurefindel.finance.mapper.CurrencyMapper;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.repository.CurrencyRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private CurrencyMapper mapper;

    @InjectMocks
    private CurrencyService service;

    @Test
    void save_shouldMapAndPersistCurrency() {
        CurrencyRequestDto request = new CurrencyRequestDto();
        Currency entity = new Currency();
        CurrencyResponseDto response = new CurrencyResponseDto();

        when(mapper.toCurrency(request)).thenReturn(entity);
        when(currencyRepository.save(entity)).thenReturn(entity);
        when(mapper.toCurrencyResponseDto(entity)).thenReturn(response);

        CurrencyResponseDto result = service.save(request);

        assertEquals(response, result);
    }

    @Test
    void update_shouldOverwriteCodeAndName() {
        CurrencyRequestDto request = new CurrencyRequestDto();
        request.setCode("EUR");
        request.setName("Euro");

        Currency entity = new Currency();
        entity.setCode("USD");
        entity.setName("Dollar");

        CurrencyResponseDto response = new CurrencyResponseDto();

        when(currencyRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(currencyRepository.save(entity)).thenReturn(entity);
        when(mapper.toCurrencyResponseDto(entity)).thenReturn(response);

        CurrencyResponseDto result = service.update(1L, request);

        assertEquals("EUR", entity.getCode());
        assertEquals("Euro", entity.getName());
        assertEquals(response, result);
    }

    @Test
    void getAll_shouldMapRepositoryResults() {
        Currency entity = new Currency();
        CurrencyResponseDto response = new CurrencyResponseDto();

        when(currencyRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toCurrencyResponseDto(entity)).thenReturn(response);

        List<CurrencyResponseDto> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }
}
