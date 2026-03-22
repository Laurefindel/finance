package com.laurefindel.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for user registration and update")
public class UserRequestDto {

    @Schema(example = "Anna")
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    
    @Schema(example = "Petrova")
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @Schema(example = "anna.petrova@example.com")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(example = "StrongPass123")
    @NotBlank(message = "Password cannot be blank")
    private String password;    

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
