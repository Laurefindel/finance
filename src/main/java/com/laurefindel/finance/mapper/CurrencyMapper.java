package com.laurefindel.finance.mapper;

import com.laurefindel.finance.dto.CurrencyRequestDto;
import com.laurefindel.finance.dto.CurrencyResponseDto;
import com.laurefindel.finance.model.entity.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {
    
    public CurrencyResponseDto toCurrencyResponseDto(Currency currency);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    @Mapping(target = "financialOperations", ignore = true)
    public Currency toCurrency(CurrencyRequestDto currencyRequestDto);
}
