package com.example.smartnotesfrontend.data.model;

public class RegisterRequest {
    private String email;
    private String password;
    private String deviceToken;

    public RegisterRequest(String email, String password, String deviceToken) {
        this.email = email;
        this.password = password;
        this.deviceToken = deviceToken;
    }
}