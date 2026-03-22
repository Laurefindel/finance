package com.laurefindel.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request payload for account creation")
public class AccountRequestDto {

    @Schema(example = "1")
    @NotNull(message = "User ID cannot be null")
    @Positive
    private Long userId;
    
    @Schema(example = "1")
    @NotNull(message = "Currency ID cannot be null")
    @Positive
    private Long currencyId;

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
