package com.laurefindel.finance.mapper;

import com.laurefindel.finance.dto.CurrencyRequestDto;
import com.laurefindel.finance.dto.CurrencyResponseDto;
import com.laurefindel.finance.model.entity.Currency;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-23T01:57:17+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Arch Linux)"
)
@Component
public class CurrencyMapperImpl implements CurrencyMapper {

    @Override
    public CurrencyResponseDto toCurrencyResponseDto(Currency currency) {
        if ( currency == null ) {
            return null;
        }

        CurrencyResponseDto currencyResponseDto = new CurrencyResponseDto();

        currencyResponseDto.setCode( currency.getCode() );
        currencyResponseDto.setName( currency.getName() );
        currencyResponseDto.setId( currency.getId() );

        return currencyResponseDto;
    }

    @Override
    public Currency toCurrency(CurrencyRequestDto currencyRequestDto) {
        if ( currencyRequestDto == null ) {
            return null;
        }

        Currency currency = new Currency();

        currency.setCode( currencyRequestDto.getCode() );
        currency.setName( currencyRequestDto.getName() );

        return currency;
    }
}
