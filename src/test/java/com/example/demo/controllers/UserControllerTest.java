package com.example.demo.controllers;

import com.example.demo.dtos.UserVerificationDTO;
import com.example.demo.services.UserService;
import com.example.demo.dtos.UserDTO;
import com.example.demo.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testRegisterUser() throws Exception {
        // GIVEN
        String userJson = "{\"username\":\"testuser\",\"email\":\"test@example.com\",\"phoneNumber\":\"48669822875\",\"password\":\"password\"}";
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPhoneNumber("48669822875");

        // WHEN
        doAnswer(invocation -> {
            UserDTO argument = invocation.getArgument(0);
            assertEquals(user.getUsername(), argument.getUsername());
            assertEquals(user.getEmail(), argument.getEmail());
            assertEquals(user.getPhoneNumber(), argument.getPhoneNumber());
            return null;
        }).when(userService).register(any(UserDTO.class));

        // THEN
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testActivateUser() throws Exception {
        // GIVEN
        String userVerificationJson = "{\"username\":\"testuser\",\"verificationCode\":\"123456\"}";
        User user = new User();
        user.setUsername("testuser");
        user.setVerificationCode("123456");

        // WHEN
        doAnswer(invocation -> {
            UserVerificationDTO argument = invocation.getArgument(0);
            assertEquals(user.getUsername(), argument.getUsername());
            assertEquals(user.getVerificationCode(), argument.getVerificationCode());
            return null;
        }).when(userService).activate(any(UserVerificationDTO.class));

        // THEN
        mockMvc.perform(post("/users/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userVerificationJson))
                .andExpect(status().isOk());
    }
}