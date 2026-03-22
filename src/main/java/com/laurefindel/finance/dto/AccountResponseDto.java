package com.laurefindel.finance.dto;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Account response payload")
public class AccountResponseDto {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "1200.50")
    private BigDecimal balance;

    @Schema(description = "Account owner")
    private UserResponseDto user;  

    @Schema(description = "Account currency")
    private CurrencyResponseDto currency;

    @Schema(description = "Outgoing operations")
    private List<FinancialOperationResponseDto> outcomingOperations;

    @Schema(description = "Incoming operations")
    private List<FinancialOperationResponseDto> incomingOperations;


    public UserResponseDto getUser() {
        return user;
    }
  
    public void setUser(UserResponseDto user) {
        this.user = user;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public CurrencyResponseDto getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyResponseDto currency) {
        this.currency = currency;
    }

    public List<FinancialOperationResponseDto> getOutcomingOperations() {
        return outcomingOperations;
    }

    public void setOutcomingOperations(List<FinancialOperationResponseDto> outcomingOperations) {
        this.outcomingOperations = outcomingOperations;
    }

    public List<FinancialOperationResponseDto> getIncomingOperations() {
        return incomingOperations;
    }

    public void setIncomingOperations(List<FinancialOperationResponseDto> incomingOperations) {
        this.incomingOperations = incomingOperations;
    }
}
