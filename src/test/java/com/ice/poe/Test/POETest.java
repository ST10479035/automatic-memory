/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ice.poe.Test;

import com.ice.poe.POE;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Simphiwe Jijana
 */
public class POETest {
    @Test
    void testCheckCellPhoneValid() {
        assertTrue(POE.checkCellPhone("+27821234567"));
        assertTrue(POE.checkCellPhone("+27609876543"));
    }

    @Test
    void testCheckCellPhoneInvalidFormat() {
        assertFalse(POE.checkCellPhone("27821234567"));
        assertFalse(POE.checkCellPhone("+2782123456"));
        assertFalse(POE.checkCellPhone("+278212345678"));
        assertFalse(POE.checkCellPhone("+27abc123456"));
        assertFalse(POE.checkCellPhone(""));
    }

    @Test
    void testIsValidUsernameValid() {
        assertTrue(POE.isValidUsername("user_1"));
        assertTrue(POE.isValidUsername("a_b"));
        assertTrue(POE.isValidUsername("_123"));
    }

    @Test
    void testIsValidUsernameInvalidLength() {
        assertFalse(POE.isValidUsername("toolong_"));
        assertFalse(POE.isValidUsername("short"));
        assertFalse(POE.isValidUsername(""));
    }

    @Test
    void testIsValidUsernameMissingUnderscore() {
        assertFalse(POE.isValidUsername("user1"));
        assertFalse(POE.isValidUsername("abcde"));
    }

    @Test
    void testIsValidPasswordValid() {
        assertTrue(POE.isValidPassword("P@sswOrd1"));
        assertTrue(POE.isValidPassword("1Special!"));
        assertTrue(POE.isValidPassword("Abcdefg#9"));
    }

    @Test
    void testIsValidPasswordInvalidLength() {
        assertFalse(POE.isValidPassword("Short1!"));
        assertFalse(POE.isValidPassword("P@ss1"));
    }

    @Test
    void testIsValidPasswordMissingUppercase() {
        assertFalse(POE.isValidPassword("p@ssword1"));
    }

    @Test
    void testIsValidPasswordMissingLowercase() {
        assertFalse(POE.isValidPassword("PASSWORD1!"));
    }

    @Test
    void testIsValidPasswordMissingSpecialChar() {
        assertFalse(POE.isValidPassword("Password1"));
    }

    @Test
    void testIsValidPasswordMissingNumber() {
        assertFalse(POE.isValidPassword("P@ssword!"));
    }
}
