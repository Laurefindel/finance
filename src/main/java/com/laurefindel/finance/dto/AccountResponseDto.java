package com.laurefindel.finance.dto;

import java.math.BigDecimal;
import java.util.List;

public class AccountResponseDto {

    private Long id;
    private BigDecimal balance;
    private UserResponseDto user;  
    private CurrencyResponseDto currency;
    private List<FinancialOperationResponseDto> outcomingOperations;
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
