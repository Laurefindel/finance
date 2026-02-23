package com.laurefindel.finance.mapper;

import com.laurefindel.finance.dto.FinancialOperationRequestDto;
import com.laurefindel.finance.dto.FinancialOperationResponseDto;
import com.laurefindel.finance.model.entity.Account;
import com.laurefindel.finance.model.entity.Currency;
import com.laurefindel.finance.model.entity.FinancialOperation;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-23T01:57:17+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Arch Linux)"
)
@Component
public class FinancialOperationMapperImpl implements FinancialOperationMapper {

    @Override
    public FinancialOperationResponseDto toFinancialOperationResponseDto(FinancialOperation financialOperation) {
        if ( financialOperation == null ) {
            return null;
        }

        FinancialOperationResponseDto financialOperationResponseDto = new FinancialOperationResponseDto();

        financialOperationResponseDto.setSenderAccountId( financialOperationSenderAccountId( financialOperation ) );
        financialOperationResponseDto.setReceiverAccountId( financialOperationReceiverAccountId( financialOperation ) );
        financialOperationResponseDto.setCurrencyCode( financialOperationCurrencyCode( financialOperation ) );
        financialOperationResponseDto.setDescription( financialOperation.getDescription() );
        financialOperationResponseDto.setAmount( financialOperation.getAmount() );
        financialOperationResponseDto.setId( financialOperation.getId() );

        return financialOperationResponseDto;
    }

    @Override
    public FinancialOperation toFinancialOperation(FinancialOperationRequestDto financialOperationRequestDto, Account sender, Account receiver, Currency currency) {
        if ( financialOperationRequestDto == null && sender == null && receiver == null && currency == null ) {
            return null;
        }

        FinancialOperation financialOperation = new FinancialOperation();

        if ( financialOperationRequestDto != null ) {
            financialOperation.setAmount( financialOperationRequestDto.getAmount() );
            financialOperation.setDescription( financialOperationRequestDto.getDescription() );
        }
        financialOperation.setSenderAccount( sender );
        financialOperation.setReceiverAccount( receiver );
        financialOperation.setCurrency( currency );

        return financialOperation;
    }

    private Long financialOperationSenderAccountId(FinancialOperation financialOperation) {
        if ( financialOperation == null ) {
            return null;
        }
        Account senderAccount = financialOperation.getSenderAccount();
        if ( senderAccount == null ) {
            return null;
        }
        long id = senderAccount.getId();
        return id;
    }

    private Long financialOperationReceiverAccountId(FinancialOperation financialOperation) {
        if ( financialOperation == null ) {
            return null;
        }
        Account receiverAccount = financialOperation.getReceiverAccount();
        if ( receiverAccount == null ) {
            return null;
        }
        long id = receiverAccount.getId();
        return id;
    }

    private String financialOperationCurrencyCode(FinancialOperation financialOperation) {
        if ( financialOperation == null ) {
            return null;
        }
        Currency currency = financialOperation.getCurrency();
        if ( currency == null ) {
            return null;
        }
        String code = currency.getCode();
        if ( code == null ) {
            return null;
        }
        return code;
    }
}
