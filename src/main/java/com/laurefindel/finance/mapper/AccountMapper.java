package com.laurefindel.finance.mapper;

import com.laurefindel.finance.dto.AccountRequestDto;
import com.laurefindel.finance.dto.AccountResponseDto;
import com.laurefindel.finance.model.entity.Account;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CurrencyMapper.class, FinancialOperationMapper.class})
public interface AccountMapper {

    @Mapping(source = "outcomingOperations", target = "outcomingOperations")
    @Mapping(source = "incomingOperations", target = "incomingOperations")
    AccountResponseDto toAccountResponseDto(Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "outcomingOperations", ignore = true)
    @Mapping(target = "incomingOperations", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "balance", ignore = true)
    Account toAccount(AccountRequestDto dto);
}