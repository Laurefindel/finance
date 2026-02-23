package com.laurefindel.finance.dto;

import java.math.BigDecimal;

public class FinancialOperationRequestDto {

    private Long senderAccountId; 
    private Long receiverAccountId;
    private BigDecimal amount;
    private String description;
  
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
  
    public BigDecimal getAmount() {
        return amount;
    }
  
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
  
    public String getDescription() {
        return description;
    }
  
    public void setDescription(String description) {
        this.description = description;
    }
}
