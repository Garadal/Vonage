package com.example.demo.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PhoneNumberValidatorTest {

    private final PhoneNumberValidator validator = new PhoneNumberValidator();

    @Test
    public void testValidPhoneNumber() {
        // Example of a valid phone number
        assertTrue(validator.isValid("48669822875", null));
    }

    @Test
    public void testInvalidPhoneNumber() {
        // Examples of invalid phone numbers
        assertFalse(validator.isValid("048669822875", null)); // Starts with 0
        assertFalse(validator.isValid("+48669822875", null)); // Contains a plus sign
        assertFalse(validator.isValid("486 698 228 75", null)); // Contains spaces
        assertFalse(validator.isValid("123", null)); // Too short
        assertFalse(validator.isValid("486698228751234567890", null)); // Too long
    }
}