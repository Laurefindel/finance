package com.laurefindel.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Currency response payload")
public class CurrencyResponseDto {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "USD")
    private String code;

    @Schema(example = "US Dollar")
    private String name;

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
