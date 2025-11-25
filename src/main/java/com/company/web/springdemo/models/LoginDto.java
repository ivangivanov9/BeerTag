package com.company.web.springdemo.models;

import jakarta.validation.constraints.NotBlank;

public class LoginDto {

    @NotBlank(message = "Username can't be empty")
    private String username;

    @NotBlank(message = "Password can't be empty")
    private String password;

    public LoginDto() {}

    public LoginDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}