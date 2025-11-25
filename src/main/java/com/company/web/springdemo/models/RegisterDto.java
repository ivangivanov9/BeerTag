package com.company.web.springdemo.models;

import jakarta.validation.constraints.NotBlank;

public class RegisterDto extends LoginDto {

    @NotBlank(message = "Password confirmation can't be empty")
    private String passwordConfirm;

    @NotBlank(message = "First name can't be empty")
    private String firstName;

    @NotBlank(message = "Last name can't be empty")
    private String lastName;

    @NotBlank(message = "Email can't be empty")
    private String email;

    public RegisterDto() {}

    public RegisterDto(String username, String password, String passwordConfirm,
                       String firstName, String lastName, String email) {
        super(username, password);
        this.passwordConfirm = passwordConfirm;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
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
}