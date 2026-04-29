package com.laurefindel.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Role payload")
public class RoleDto {

    @Schema(example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @Schema(example = "User")
    @NotBlank(message = "Role name cannot be blank")
    @Size(min = 1, max = 50, message = "Role name must be between 1 and 50 characters")
    private String name;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
