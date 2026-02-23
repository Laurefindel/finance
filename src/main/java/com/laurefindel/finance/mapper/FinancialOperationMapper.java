package com.laurefindel.finance.mapper;

import com.laurefindel.finance.model.entity.Account;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.model.entity.FinancialOperation;
import com.laurefindel.finance.dto.FinancialOperationRequestDto;
import com.laurefindel.finance.dto.FinancialOperationResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FinancialOperationMapper {
    
    @Mapping(source = "senderAccount.id", target = "senderAccountId")
    @Mapping(source = "receiverAccount.id", target = "receiverAccountId")
    @Mapping(source = "currency.code", target = "currencyCode")
    public FinancialOperationResponseDto toFinancialOperationResponseDto(FinancialOperation financialOperation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senderAccount", source = "sender")
    @Mapping(target = "receiverAccount", source = "receiver")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "createdAt", ignore = true)
    public FinancialOperation toFinancialOperation(FinancialOperationRequestDto financialOperationRequestDto, Account sender, Account receiver, Currency currency);
}
