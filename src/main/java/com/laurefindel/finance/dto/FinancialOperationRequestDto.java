package com.laurefindel.finance.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating financial operation")
public class FinancialOperationRequestDto {

    @Schema(example = "1")
    @NotNull(message = "Sender account ID cannot be null")
    @Positive
    private Long senderAccountId;

    @Schema(example = "2")
    @NotNull(message = "Receiver account ID cannot be null")
    @Positive
    private Long receiverAccountId;

    @Schema(example = "150.75")
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0", message = "Amount must be positive")
    private BigDecimal amount;

    @Schema(example = "Transfer for lunch")
    @Size(max = 255, message = "Description cannot be longer than 255 characters")
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
