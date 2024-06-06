package com.example.demo.services;

import com.example.demo.dtos.UserDTO;
import com.example.demo.dtos.UserVerificationDTO;
import com.example.demo.models.User;

public interface IUserService {
    User register(UserDTO userDTO);
    User activate(UserVerificationDTO userVerificationDTO);
    void deleteUnverifiedUsers();
}
