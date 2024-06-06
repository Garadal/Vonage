package com.example.demo.models;

import lombok.Data;

// Currently not used, but might be handy if application grows
@Data
public class UserVerificationRequest {
    private String username;
    private String verificationCode;
}