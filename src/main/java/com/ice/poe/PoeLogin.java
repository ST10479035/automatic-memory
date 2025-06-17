/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ice.poe;

import javax.swing.JOptionPane;
import java.util.Map;

/**
 * Handles the user login process for the QuickChat application.
 * It takes a map of registered user details and manages login attempts.
 *
 * @author Simphiwe Jijana
 */
public class PoeLogin {

    private Map<String, UserDetails> registeredUsers;
    private int maxLoginAttempts;
    private boolean isLoggedIn;

    /**
     * Constructor for PoeLogin.
     *
     * @param registeredUsers A map where the key is the username (String)
     * and the value is a UserDetails object containing
     * the user's first name, last name, and password.
     * @param maxLoginAttempts The maximum number of failed login attempts allowed.
     */
    public PoeLogin(Map<String, UserDetails> registeredUsers, int maxLoginAttempts) {
        this.registeredUsers = registeredUsers;
        this.maxLoginAttempts = maxLoginAttempts;
        this.isLoggedIn = false; // Initially, the user is not logged in
    }

    /**
     * Performs the login process. It prompts the user for a username and password,
     * validates them against the registered user details, and manages login attempts.
     * If login is successful, sets the isLoggedIn flag to true.
     */
    public void performLogin() {
        int attempts = 0;
        while (attempts < maxLoginAttempts && !isLoggedIn) {
            String enteredUsername = JOptionPane.showInputDialog(null,
                    "Enter your username:",
                    "Login",
                    JOptionPane.QUESTION_MESSAGE);

            // Handle user cancelling the login dialog
            if (enteredUsername == null) {
                JOptionPane.showMessageDialog(null, "Login cancelled.", "Login", JOptionPane.INFORMATION_MESSAGE);
                isLoggedIn = false; // Ensure loggedIn status is false
                return; // Exit the login process
            }

            String enteredPassword = JOptionPane.showInputDialog(null,
                    "Enter your password:",
                    "Login",
                    JOptionPane.QUESTION_MESSAGE);

            // Handle user cancelling the login dialog
            if (enteredPassword == null) {
                JOptionPane.showMessageDialog(null, "Login cancelled.", "Login", JOptionPane.INFORMATION_MESSAGE);
                isLoggedIn = false; // Ensure loggedIn status is false
                return; // Exit the login process
            }

            // Validate credentials
            if (validateLogin(enteredUsername, enteredPassword)) {
                isLoggedIn = true;
                JOptionPane.showMessageDialog(null, "Login successful!");
            } else {
                attempts++;
                int remainingAttempts = maxLoginAttempts - attempts;
                if (remainingAttempts > 0) {
                    JOptionPane.showMessageDialog(null,
                            "Incorrect username or password. You have " + remainingAttempts + " attempts remaining.",
                            "Login Failed",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "You have exceeded your login attempts. Application will now exit.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Validates the provided username and password against the registered user details.
     *
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return true if the username and password match a registered user, false otherwise.
     */
    public boolean validateLogin(String username, String password) {
        // Check if the username exists in the registeredUsers map
        if (registeredUsers.containsKey(username)) {
            UserDetails userDetails = registeredUsers.get(username);
            // Compare the entered password with the stored password
            return userDetails.getPassword().equals(password);
        }
        return false; // Username not found
    }

    /**
     * Checks if the user is currently logged in.
     *
     * @return true if the user is logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}