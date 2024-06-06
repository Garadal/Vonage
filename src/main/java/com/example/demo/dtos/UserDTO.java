package com.example.demo.dtos;

import com.example.demo.validation.ValidPhoneNumber;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;


import jakarta.validation.constraints.NotBlank;

@Data
@Valid
public class UserDTO {
    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number is mandatory")
    @ValidPhoneNumber(message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}