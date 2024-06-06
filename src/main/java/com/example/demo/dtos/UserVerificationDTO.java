package com.example.demo.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UserVerificationDTO {
    @NotEmpty(message = "Username is mandatory")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotEmpty(message = "Verification code is mandatory")
    @Pattern(regexp = "^.{4,6}$", message = "Verification code must be exactly 4 or 6 characters long")
    private String verificationCode;
}