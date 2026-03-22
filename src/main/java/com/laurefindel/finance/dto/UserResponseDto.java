package com.laurefindel.finance.dto;

import java.util.List;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User response payload")
public class UserResponseDto {
    @Schema(example = "1")
    private Long id;

    @Schema(example = "Anna")
    private String firstName;

    @Schema(example = "Petrova")
    private String lastName;

    @Schema(example = "anna.petrova@example.com")
    private String email;

    @Schema(example = "ACTIVE")
    private String status;

    @Schema(example = "[1, 2]")
    private List<Long> accountsIds;

    @Schema(example = "[1]")
    private Set<Long> roleIds;

    public Long getId() {
        return id;
    }  

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Long> getAccountsIds() {
        return accountsIds;
    } 

    public void setAccountsIds(List<Long> accountsIds) {
        this.accountsIds = accountsIds;
    }

    public Set<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }
}