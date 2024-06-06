package com.example.demo.services;

import com.example.demo.dtos.UserDTO;
import com.example.demo.dtos.UserVerificationDTO;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.vonage.client.VonageClient;
import com.vonage.client.verify.CheckResponse;
import com.vonage.client.verify.VerifyResponse;
import com.vonage.client.verify.VerifyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vonage.client.verify.VerifyClient;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VonageClient vonageClient;

    @Mock
    private VerifyClient verifyClient;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        // Make vonageClient.getVerifyClient() return verifyClient
        Mockito.lenient().when(vonageClient.getVerifyClient()).thenReturn(verifyClient);
    }

    // Test method to verify successful user registration
    @Test
    public void testRegisterUserSuccess() {
        // GIVEN: A user DTO with valid information
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setPhoneNumber("48669822875");
        userDTO.setPassword("password");

        // Mock VerifyClient's response
        VerifyResponse verifyResponse = Mockito.mock(VerifyResponse.class);
        when(verifyResponse.getStatus()).thenReturn(VerifyStatus.OK);
        when(verifyResponse.getRequestId()).thenReturn("request-id-123");

        // Mock UserRepository to return no existing user
        when(verifyClient.verify(eq("48669822875"), eq("Vonage"))).thenReturn(verifyResponse);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN: Calling the register method
        User registeredUser = userService.register(userDTO);

        // THEN: Verify that the user is registered successfully
        assertNotNull(registeredUser);
        assertEquals("testuser", registeredUser.getUsername());
        assertEquals("test@example.com", registeredUser.getEmail());
        assertEquals("48669822875", registeredUser.getPhoneNumber());
        assertFalse(registeredUser.isVerified());
        assertEquals("request-id-123", registeredUser.getVerificationCode());
    }

    // Test method to verify registration failure due to existing username
    @Test
    public void testRegisterUserUsernameExists() {
        // GIVEN: A user DTO with an existing username
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setPhoneNumber("48669822875");
        userDTO.setPassword("password");

        // Mock UserRepository to return an existing user
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(new User()));

        // WHEN: Calling the register method with the existing username
        Exception exception = assertThrows(RuntimeException.class, () -> userService.register(userDTO));

        // THEN: Verify that the appropriate exception is thrown
        assertEquals("Username already exists", exception.getMessage());
    }

    // Test method to verify successful user activation
    @Test
    public void testActivateUserSuccess() {
        // GIVEN: A user verification DTO and a user with a verification code
        UserVerificationDTO userVerificationDTO = new UserVerificationDTO();
        userVerificationDTO.setUsername("testuser");
        userVerificationDTO.setVerificationCode("123456");

        User user = new User();
        user.setUsername("testuser");
        user.setVerificationCode("request-id-123");
        user.setVerified(false);

        // Mock CheckResponse from VerifyClient
        CheckResponse checkResponse = Mockito.mock(CheckResponse.class);
        when(checkResponse.getStatus()).thenReturn(VerifyStatus.OK);

        // Mock UserRepository to return the user
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(verifyClient.check(eq("request-id-123"), eq("123456"))).thenReturn(checkResponse);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN: Calling the activate method
        User activatedUser = userService.activate(userVerificationDTO);

        // THEN: Verify that the user is activated successfully
        assertNotNull(activatedUser);
        assertTrue(activatedUser.isVerified());
    }

    // Test method to verify user activation failure due to invalid verification code
    @Test
    public void testActivateUserInvalidCode() {
        // GIVEN: A user verification DTO and a user with a verification code
        UserVerificationDTO userVerificationDTO = new UserVerificationDTO();
        userVerificationDTO.setUsername("testuser");
        userVerificationDTO.setVerificationCode("123456");

        User user = new User();
        user.setUsername("testuser");
        user.setVerificationCode("request-id-123");
        user.setVerified(false);

        // Mock CheckResponse from VerifyClient to indicate an invalid code
        CheckResponse checkResponse = Mockito.mock(CheckResponse.class);
        when(checkResponse.getStatus()).thenReturn(VerifyStatus.INVALID_REQUEST);
        when(checkResponse.getErrorText()).thenReturn("Invalid code");

        // Mock UserRepository to return the user
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(verifyClient.check(eq("request-id-123"), eq("123456"))).thenReturn(checkResponse);


        // WHEN: Calling the activate method
        Exception exception = assertThrows(RuntimeException.class, () -> userService.activate(userVerificationDTO));

        // THEN: Verify that the appropriate exception is thrown
        assertEquals("Invalid verification code: Invalid code", exception.getMessage());
    }
}