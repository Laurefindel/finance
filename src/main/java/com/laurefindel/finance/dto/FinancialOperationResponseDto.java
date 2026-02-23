package com.laurefindel.finance.dto;

import java.math.BigDecimal;

public class FinancialOperationResponseDto {
    private Long id;
    private Long senderAccountId;
    private Long receiverAccountId;
    private String description;
    private BigDecimal amount;
    private String currencyCode; 


    public Long getSenderAccountId() {
        return senderAccountId;
    }
    public void setSenderAccountId(Long senderAccountId) {
        this.senderAccountId = senderAccountId;
    }
    public Long getReceiverAccountId() {
        return receiverAccountId;
    }
    public void setReceiverAccountId(Long receiverAccountId) {
        this.receiverAccountId = receiverAccountId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCurrencyCode() {
        return currencyCode;
    }
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    
}
