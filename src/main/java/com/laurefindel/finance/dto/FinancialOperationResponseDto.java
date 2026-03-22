package com.laurefindel.finance.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Financial operation response payload")
public class FinancialOperationResponseDto {
    @Schema(example = "1")
    private Long id;

    @Schema(example = "1")
    private Long senderAccountId;

    @Schema(example = "2")
    private Long receiverAccountId;

    @Schema(example = "Transfer for lunch")
    private String description;

    @Schema(example = "150.75")
    private BigDecimal amount;

    @Schema(example = "USD")
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
