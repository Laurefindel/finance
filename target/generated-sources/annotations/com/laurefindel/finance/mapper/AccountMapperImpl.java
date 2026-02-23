package com.laurefindel.finance.mapper;

import com.laurefindel.finance.dto.AccountRequestDto;
import com.laurefindel.finance.dto.AccountResponseDto;
import com.laurefindel.finance.dto.FinancialOperationResponseDto;
import com.laurefindel.finance.model.entity.Account;
import com.laurefindel.finance.model.entity.FinancialOperation;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-23T01:57:17+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Arch Linux)"
)
@Component
public class AccountMapperImpl implements AccountMapper {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CurrencyMapper currencyMapper;
    @Autowired
    private FinancialOperationMapper financialOperationMapper;

    @Override
    public AccountResponseDto toAccountResponseDto(Account account) {
        if ( account == null ) {
            return null;
        }

        AccountResponseDto accountResponseDto = new AccountResponseDto();

        accountResponseDto.setOutcomingOperations( financialOperationListToFinancialOperationResponseDtoList( account.getOutcomingOperations() ) );
        accountResponseDto.setIncomingOperations( financialOperationListToFinancialOperationResponseDtoList( account.getIncomingOperations() ) );
        accountResponseDto.setUser( userMapper.toUserResponseDto( account.getUser() ) );
        accountResponseDto.setId( account.getId() );
        accountResponseDto.setBalance( account.getBalance() );
        accountResponseDto.setCurrency( currencyMapper.toCurrencyResponseDto( account.getCurrency() ) );

        return accountResponseDto;
    }

    @Override
    public Account toAccount(AccountRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Account account = new Account();

        account.setBalance( dto.getBalance() );
        account.setStatus( dto.getStatus() );

        return account;
    }

    protected List<FinancialOperationResponseDto> financialOperationListToFinancialOperationResponseDtoList(List<FinancialOperation> list) {
        if ( list == null ) {
            return null;
        }

        List<FinancialOperationResponseDto> list1 = new ArrayList<FinancialOperationResponseDto>( list.size() );
        for ( FinancialOperation financialOperation : list ) {
            list1.add( financialOperationMapper.toFinancialOperationResponseDto( financialOperation ) );
        }

        return list1;
    }
}
