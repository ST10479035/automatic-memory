/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.ice.poe;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner; // Still needed for console input during registration/initial login
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class serves as the main entry point for the POE application. It handles
 * user registration by collecting necessary information such as username,
 * password, first name, last name, and cell phone number. It performs
 * validation on the entered data and then initiates the login process.
 *
 * @author Simphiwe Jijana
 */
public class POE {

    private static final String JSON_FILE_PATH = "messages.json";
    private static Map<String, UserDetails> userDetailsMap = new HashMap<>(); // Store registered users
    
    // Declare a list to store sent messages at the class level
    private static List<Message> sessionSentMessages = new ArrayList<>();

    // Methods from POE.java for validation
    public static boolean checkCellPhone(String number) {
        String pattern = "^\\+27\\d{9}$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(number);
        return matcher.matches();
    }

    public static boolean isValidCellPhoneNumber(String cellPhoneNumber) {
        return checkCellPhone(cellPhoneNumber);
    }

    public static boolean isValidUsername(String username) {
        return username.length() <= 5 && username.contains("_");
    }

    public static boolean isValidPassword(String password) {
        return password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*[a-z].*") && password.matches(".*[!@#$%^&*].*") && password.matches(".*[0-9].*");
    }

    public static void main(String[] args) {
        // User Registration 
        try (Scanner scanner = new Scanner(System.in)) { // Scanner is used for console input in registration if you revert to it
            JOptionPane.showMessageDialog(null, "Welcome to QuickChat! First, let's register your account.");

            String regUsername;
            String regPassword;
            String regCellPhoneNumber;
            String regFirstName;
            String regLastName;

            // Get first name
            regFirstName = JOptionPane.showInputDialog(null, "Please enter your first name:");
            if (regFirstName == null || regFirstName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "First name cannot be empty. Exiting application.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get last name
            regLastName = JOptionPane.showInputDialog(null, "Please enter your last name:");
            if (regLastName == null || regLastName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Last name cannot be empty. Exiting application.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get and validate username
            do {
                regUsername = JOptionPane.showInputDialog(null, "Please enter username (Max of 5 characters, must contain an underscore):");
                if (regUsername == null) { // User cancelled
                    JOptionPane.showMessageDialog(null, "Registration cancelled. Exiting application.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!isValidUsername(regUsername)) {
                    JOptionPane.showMessageDialog(null, "Username is not correctly formatted.\nPlease ensure your username contains an underscore and is no more than five characters in length.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } while (!isValidUsername(regUsername));
            JOptionPane.showMessageDialog(null, "Username successfully captured.");

            // Get and validate password
            do {
                regPassword = JOptionPane.showInputDialog(null, "Please enter password (Password must contain at least 8 characters, a capital letter, a number, and a special character):");
                if (regPassword == null) { // User cancelled
                    JOptionPane.showMessageDialog(null, "Registration cancelled. Exiting application.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!isValidPassword(regPassword)) {
                    JOptionPane.showMessageDialog(null, "Password is not correctly formatted.\nPlease ensure the password contains at least eight characters, a capital letter, a number, and a special character.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } while (!isValidPassword(regPassword));
            JOptionPane.showMessageDialog(null, "Password successfully captured.");

            // Get and validate cell phone number
            do {
                regCellPhoneNumber = JOptionPane.showInputDialog(null, "Please enter a South African phone number (Beginning with the code +27 followed by 9 digits):");
                if (regCellPhoneNumber == null) { // User cancelled
                    JOptionPane.showMessageDialog(null, "Registration cancelled. Exiting application.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!isValidCellPhoneNumber(regCellPhoneNumber)) {
                    JOptionPane.showMessageDialog(null, "Cell phone number incorrectly formatted.\nPlease ensure it begins with '+27' and is followed by 9 digits.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } while (!isValidCellPhoneNumber(regCellPhoneNumber));
            JOptionPane.showMessageDialog(null, "Cell phone number successfully added.");

            // Store the registered user details
            userDetailsMap.put(regUsername, new UserDetails(regFirstName, regLastName, regPassword));
            JOptionPane.showMessageDialog(null, "Registration complete! Now, please log in.");

            // Login Process 
            PoeLogin loginSystem = new PoeLogin(userDetailsMap, 3); // Assuming max 3 login attempts
            loginSystem.performLogin();

            if (loginSystem.isLoggedIn()) {
                JOptionPane.showMessageDialog(null, "Welcome to QuickChat.");

                int choice;
                do {
                    String menuInput = JOptionPane.showInputDialog(null,
                            "Select an option:\n" +
                                    "1) Send Messages\n" +
                                    "2) Show recently sent messages\n" + // Updated menu option
                                    "3) Quit",
                            "QuickChat Menu",
                            JOptionPane.QUESTION_MESSAGE);

                    if (menuInput == null) { // Handle user clicking cancel or closing dialog
                        choice = 3; // Treat as quit
                    } else {
                        try {
                            choice = Integer.parseInt(menuInput);
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
                            choice = 0; // Invalid choice, loop again
                        }
                    }

                    switch (choice) {
                        case 1:
                            sendMessagesFlow();
                            break;
                        case 2:
                            //Call method to show last message ---
                            showAllMessages();
                            break;
                        case 3:
                            JOptionPane.showMessageDialog(null, "Thank you for using QuickChat. Goodbye!");
                            break;
                        default:
                            if (choice != 0) { // Avoid double error message if it was NumberFormatException
                                JOptionPane.showMessageDialog(null, "Invalid option. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                    }
                } while (choice != 3);
            } else {
                JOptionPane.showMessageDialog(null, "Login failed. Exiting application.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void sendMessagesFlow() {
        String numMessagesInput = JOptionPane.showInputDialog(null,
                "How many messages do you wish to enter?",
                "Number of Messages",
                JOptionPane.QUESTION_MESSAGE);

        int numberOfMessages;
        if (numMessagesInput == null) {
            return; // User cancelled
        }
        try {
            numberOfMessages = Integer.parseInt(numMessagesInput);
            if (numberOfMessages <= 0) {
                JOptionPane.showMessageDialog(null, "Please enter a positive number of messages.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // List<Message> sentMessages = new ArrayList<>();
        // Now using the class-level 'sessionSentMessages'

        for (int i = 0; i < numberOfMessages; i++) {
            Message message = new Message(i);

            String recipientCell;
            boolean validRecipient = false;
            do {
                recipientCell = JOptionPane.showInputDialog(null,
                        "Enter recipient cell number (e.g., +27123456789):",
                        "Message " + (i + 1) + " of " + numberOfMessages,
                        JOptionPane.QUESTION_MESSAGE);

                if (recipientCell == null) {
                    return;
                }

                if (isValidCellPhoneNumber(recipientCell)) {
                    message.setRecipientCell(recipientCell);
                    validRecipient = true;
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid recipient number. Must start with '+27' and be followed by 9 digits.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } while (!validRecipient);

            String messageContent;
            boolean validMessageContent = false;
            do {
                messageContent = JOptionPane.showInputDialog(null,
                        "Enter message (max 250 characters):",
                        "Message " + (i + 1) + " of " + numberOfMessages,
                        JOptionPane.QUESTION_MESSAGE);

                if (messageContent == null) {
                    return;
                }

                if (message.checkMessageLength(messageContent)) {
                    message.setMessageContent(messageContent);
                    validMessageContent = true;
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a message of less than 250 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } while (!validMessageContent);

            message.createMessageHash(message.getMessageID(), i, message.getMessageContent());

            String[] options = {"Send Message", "Disregard Message", "Store Message"};
            int messageOption = JOptionPane.showOptionDialog(null,
                    "What would you like to do with this message?",
                    "Message Options",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            switch (messageOption) {
                case JOptionPane.YES_OPTION: // Send Message
                    JOptionPane.showMessageDialog(null, message.sendMessage());
                    message.printMessages(); // Display details after sending
                    //  Add to the class-level list ---
                    sessionSentMessages.add(message);
                    break;
                case JOptionPane.NO_OPTION: // Disregard Message
                    JOptionPane.showMessageDialog(null, "Message disregarded.");
                    break;
                case JOptionPane.CANCEL_OPTION: // Store Message
                case -1: // Dialog closed
                    message.storeMessage(JSON_FILE_PATH);
                    // If you also want stored messages to be part of "recent", add them here:
                    // sessionSentMessages.add(message);
                    break;
            }
        }
        JOptionPane.showMessageDialog(null, "Total messages sent: " + Message.getTotalMessagesSent(), "Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    //Method to show the last sent message ---
    private static void showAllMessages() {
        if (sessionSentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages have been sent yet in this session.", "All Sent Messages", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder allMessagesDisplay = new StringBuilder("--- All Sent Messages ---\n\n");
            for (int i = 0; i < sessionSentMessages.size(); i++) {
                Message msg = sessionSentMessages.get(i);
                allMessagesDisplay.append("Message ").append(i + 1).append(":\n");
                allMessagesDisplay.append("  Recipient: ").append(msg.getRecipientCell()).append("\n");
                allMessagesDisplay.append("  Message: ").append(msg.getMessageContent()).append("\n");
                allMessagesDisplay.append("  Hash: ").append(msg.getMessageHash()).append("\n");
                allMessagesDisplay.append("----------------------------\n");
            }
            // JOptionPane can handle longer strings, but it might get truncated or require scrolling for many messages.
            JOptionPane.showMessageDialog(null, allMessagesDisplay.toString(), "All Sent Messages", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}