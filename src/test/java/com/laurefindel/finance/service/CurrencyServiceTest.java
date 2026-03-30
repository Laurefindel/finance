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
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
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

    @Test
    void getById_shouldReturnMappedCurrency() {
        Currency entity = new Currency();
        CurrencyResponseDto response = new CurrencyResponseDto();

        when(currencyRepository.findById(7L)).thenReturn(Optional.of(entity));
        when(mapper.toCurrencyResponseDto(entity)).thenReturn(response);

        CurrencyResponseDto result = service.getById(7L);

        assertEquals(response, result);
    }

    @Test
    void getByCode_shouldReturnMappedCurrency() {
        Currency entity = new Currency();
        CurrencyResponseDto response = new CurrencyResponseDto();

        when(currencyRepository.findByCode("USD")).thenReturn(entity);
        when(mapper.toCurrencyResponseDto(entity)).thenReturn(response);

        CurrencyResponseDto result = service.getByCode("USD");

        assertEquals(response, result);
    }

    @Test
    void getByName_shouldReturnMappedCurrencies() {
        Currency entity = new Currency();
        CurrencyResponseDto response = new CurrencyResponseDto();

        when(currencyRepository.findByName("Dollar")).thenReturn(List.of(entity));
        when(mapper.toCurrencyResponseDto(entity)).thenReturn(response);

        List<CurrencyResponseDto> result = service.getByName("Dollar");

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void delete_shouldDelegateToRepository() {
        service.delete(4L);
        verify(currencyRepository).deleteById(4L);
    }

    @Test
    void getEntityByCode_shouldReturnEntity() {
        Currency entity = new Currency();
        when(currencyRepository.findByCode("EUR")).thenReturn(entity);

        Currency result = service.getEntityByCode("EUR");

        assertEquals(entity, result);
    }

    @Test
    void patch_shouldUpdateOnlyProvidedFields() {
        CurrencyRequestDto request = new CurrencyRequestDto();
        request.setName("Updated Name");

        Currency entity = new Currency();
        entity.setCode("USD");
        entity.setName("Old Name");

        CurrencyResponseDto response = new CurrencyResponseDto();

        when(currencyRepository.findById(8L)).thenReturn(Optional.of(entity));
        when(currencyRepository.save(entity)).thenReturn(entity);
        when(mapper.toCurrencyResponseDto(entity)).thenReturn(response);

        CurrencyResponseDto result = service.patch(8L, request);

        assertEquals("USD", entity.getCode());
        assertEquals("Updated Name", entity.getName());
        assertEquals(response, result);
    }

    @Test
    void patch_shouldUpdateCodeAndKeepNameWhenNameIsNull() {
        CurrencyRequestDto request = new CurrencyRequestDto();
        request.setCode("JPY");

        Currency entity = new Currency();
        entity.setCode("USD");
        entity.setName("Dollar");

        CurrencyResponseDto response = new CurrencyResponseDto();

        when(currencyRepository.findById(9L)).thenReturn(Optional.of(entity));
        when(currencyRepository.save(entity)).thenReturn(entity);
        when(mapper.toCurrencyResponseDto(entity)).thenReturn(response);

        CurrencyResponseDto result = service.patch(9L, request);

        assertEquals("JPY", entity.getCode());
        assertEquals("Dollar", entity.getName());
        assertEquals(response, result);
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(currencyRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.getById(404L));
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        CurrencyRequestDto request = new CurrencyRequestDto();
        when(currencyRepository.findById(505L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.update(505L, request));
    }

    @Test
    void patch_shouldThrowWhenNotFound() {
        CurrencyRequestDto request = new CurrencyRequestDto();
        when(currencyRepository.findById(606L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.patch(606L, request));
    }
}
