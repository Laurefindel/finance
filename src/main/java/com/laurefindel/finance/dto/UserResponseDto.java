package com.laurefindel.finance.dto;

import java.util.List;
import java.util.Set;

public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private List<Long> accountsIds;
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