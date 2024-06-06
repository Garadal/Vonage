package com.example.demo.services;

import com.example.demo.dtos.UserDTO;
import com.example.demo.dtos.UserVerificationDTO;
import com.example.demo.exceptions.UsernameAlreadyExistsException;
import com.example.demo.exceptions.VerificationFailedException;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.vonage.client.VonageClient;
import com.vonage.client.verify.CheckResponse;
import com.vonage.client.verify.VerifyResponse;
import com.vonage.client.verify.VerifyStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final VonageClient client;

    @Autowired
    public UserService(VonageClient client, UserRepository userRepository) {
        this.client = client;
        this.userRepository = userRepository;
    }

    public void deleteUnverifiedUsers() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeMinutesAgo = now.minusMinutes(3);
        userRepository.deleteByIsVerifiedFalseAndRegistrationTimeBefore(threeMinutesAgo);
    }

    public User register(UserDTO userDTO) {
        // Check if username already exists
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        // Send verification code
        VerifyResponse response = client.getVerifyClient().verify(userDTO.getPhoneNumber(), "Vonage");

        if (response.getStatus() == VerifyStatus.OK) {
            System.out.printf("RequestID: %s", response.getRequestId());
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            user.setPhoneNumber(userDTO.getPhoneNumber());
            user.setPassword(userDTO.getPassword());
            user.setVerified(false);
            user.setRegistrationTime(LocalDateTime.now());
            user.setVerificationCode(response.getRequestId());
            return userRepository.save(user);
        } else {
            throw new VerificationFailedException(String.format("ERROR! %s: %s", response.getStatus(), response.getErrorText()));
        }
    }

    public User activate(UserVerificationDTO userVerificationDTO) {

        Optional<User> userOptional = userRepository.findByUsername((userVerificationDTO.getUsername()));

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        // Verify the code
        CheckResponse response = client.getVerifyClient().check(user.getVerificationCode(), userVerificationDTO.getVerificationCode());

        if (response.getStatus() != VerifyStatus.OK) {
            throw new RuntimeException("Invalid verification code: " + response.getErrorText());
        }

        user.setVerified(true);
        return userRepository.save(user);
    }

    //Just for testing
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}