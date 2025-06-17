/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ice.poe;

import java.io.File;
import java.io.FileReader;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONTokener;

/**
 *
 * @author Simphiwe Jijana
 */
public class Message {
    private String messageID;
    private int numMessagesSent;
    private String recipientCell;
    private String messageContent;
    private String messageHash;
    private static int totalMessagesSent = 0; // Static to track across all Message instances

    public Message(int messageIndex) {
        this.messageID = generateMessageID();
        this.numMessagesSent = messageIndex + 1; // Correctly reflects the current message number
    }

    // --- Getters ---
    public String getMessageID() {
        return messageID;
    }

    public int getNumMessagesSent() {
        return numMessagesSent;
    }

    public String getRecipientCell() {
        return recipientCell;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public static int getTotalMessagesSent() {
        return totalMessagesSent;
    }

    // --- Setters ---
    public void setRecipientCell(String recipientCell) {
        this.recipientCell = recipientCell;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    // --- Core Methods ---

    /**
     * Generates a random ten-digit unique message ID.
     *
     * @return A ten-digit string representing the message ID.
     */
    public String generateMessageID() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Checks if the provided recipient cell number is correctly formatted.
     * Assumes format starts with '+' and is up to 10 characters long.
     *
     * @param cellNumber The recipient's cell number.
     * @return True if formatted correctly, false otherwise.
     */
    public boolean checkRecipientCell(String cellNumber) {
        if (cellNumber == null || cellNumber.isEmpty()) {
            return false;
        }
        return cellNumber.startsWith("+") && cellNumber.length() <= 10;
    }

    /**
     * Checks if the message content length is valid (max 250 characters).
     *
     * @param message The message content.
     * @return True if the message length is valid, false otherwise.
     */
    public boolean checkMessageLength(String message) {
        return message != null && message.length() <= 250;
    }

    /**
     * Creates the message hash based on the defined format.
     * Format: first two numbers of Message ID : message index : first and last words of message (all caps).
     *
     * @param messageId The message ID.
     * @param messageIndex The index of the message (0-based).
     * @param messageContent The content of the message.
     * @return The generated message hash.
     */
    public String createMessageHash(String messageId, int messageIndex, String messageContent) {
        if (messageId == null || messageId.length() < 2 || messageContent == null || messageContent.trim().isEmpty()) {
            return "INVALID_HASH";
        }

        String firstTwoId = messageId.substring(0, 2);
        // Split by one or more whitespace characters and filter out empty strings
        String[] words = messageContent.trim().split("\\s+");

        String firstWord = "";
        String lastWord = "";

        if (words.length > 0) {
            firstWord = words[0].toUpperCase();
            if (words.length > 1) {
                lastWord = words[words.length - 1].toUpperCase();
            } else {
                lastWord = firstWord; // If only one word, first and last are the same
            }
        } else {
            // This case should ideally be caught by messageContent.trim().isEmpty() above,
            // but as a safeguard, if words array is empty, hash should reflect this.
            return "INVALID_HASH"; // Or handle as per requirements
        }

        this.messageHash = String.format("%s:%d:%s%s", firstTwoId, messageIndex, firstWord, lastWord);
        return this.messageHash;
    }

    /**
     * Simulates sending a message. Increments the total messages sent count.
     *
     * @return A message indicating successful sending.
     */
    public String sendMessage() {
        totalMessagesSent++;
        return "Message sent";
    }

    /**
     * Stores the message details in a JSON file.
     * This method now correctly handles appending to a JSON array.
     *
     * @param filePath The path to the JSON file.
     */
    public void storeMessage(String filePath) {
        JSONObject messageJson = new JSONObject();
        messageJson.put("MessageID", this.messageID);
        messageJson.put("NumMessagesSent", this.numMessagesSent);
        messageJson.put("Recipient", this.recipientCell);
        messageJson.put("Message", this.messageContent);
        messageJson.put("MessageHash", this.messageHash);

        JSONArray jsonArray;
        File file = new File(filePath);

        // Read existing JSON array if file exists and is not empty/corrupted
        if (file.exists() && file.length() > 0) {
            try (FileReader reader = new FileReader(filePath)) {
                // Use JSONTokener to correctly parse the file content as a JSONArray
                jsonArray = new JSONArray(new JSONTokener(reader));
            } catch (IOException | org.json.JSONException e) {
                // If file is empty or corrupted, start with a new array
                jsonArray = new JSONArray();
                JOptionPane.showMessageDialog(null, "Warning: Existing JSON file was invalid or empty, starting new array.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            jsonArray = new JSONArray();
        }

        jsonArray.put(messageJson); // Add the new message object

        // Write the entire updated JSON array back to the file
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(jsonArray.toString(4)); // Pretty print with 4 spaces indent
            JOptionPane.showMessageDialog(null, "Message stored in JSON file.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error storing message: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Displays all message details using JOptionPane.
     */
    public void printMessages() {
        String messageDetails = "Message ID: " + this.messageID + "\n" +
                                "Message Hash: " + this.messageHash + "\n" +
                                "Recipient: " + this.recipientCell + "\n" +
                                "Message: " + this.messageContent;
        JOptionPane.showMessageDialog(null, messageDetails, "Message Details", JOptionPane.INFORMATION_MESSAGE);
    }
    

    /**
     * Returns the total number of messages sent so far.
     *
     * @return The total count of messages sent.
     */
    public int returnTotalMessages() {
        return totalMessagesSent;
    }
}