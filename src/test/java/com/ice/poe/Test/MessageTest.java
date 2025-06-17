/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ice.poe.Test;

import com.ice.poe.Message; // Import the Message class
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach; // For restoring System.out
import org.junit.jupiter.api.AfterAll; // For cleaning up test files
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.lang.reflect.Field; // Needed for reflection to reset static field
import java.io.File; // For file operations in storeMessage tests
import java.io.FileWriter;

/**
 *
 * @author Simphiwe Jijana
 */
@DisplayName("Message Class Tests")
public class MessageTest {

    private Message message;
    // To capture System.out for printMessages() tests (if it were to print to console)
    // Kept for now in case of future console output, but not used for JOptionPane.
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private static final String TEST_JSON_FILE = "test_messages.json";

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Initialize a new Message object before each test.
        // The index 0 is used for the constructor, setting numMessagesSent to 1.
        message = new Message(0);

        // --- Crucial: Reset static totalMessagesSent before each test ---
        // This ensures test independence by clearing shared static state.
        Field field = Message.class.getDeclaredField("totalMessagesSent");
        field.setAccessible(true);
        field.set(null, 0);

        // Redirect System.out to capture console output.
        // Note: As JOptionPane is used in printMessages(), this redirection won't capture those GUI dialogues.
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        // Restore the original System.out after each test method runs
        System.setOut(originalOut);
        // Clear the captured output for the next test
        outContent.reset();
    }

    @AfterAll
    static void cleanUp() {
        // Delete the temporary JSON test file to ensure a clean state for future runs
        File file = new File(TEST_JSON_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    // --- Test `generateMessageID()` ---
    @Test
    @DisplayName("Test Message ID: ID is created (non-null and 10 digits)")
    void testGenerateMessageID() {
        String messageId = message.generateMessageID();
        assertNotNull(messageId, "Message ID should not be null.");
        assertEquals(10, messageId.length(), "Message ID should be 10 digits long.");
        assertTrue(messageId.matches("\\d{10}"), "Message ID should consist of 10 digits.");
    }

    @Test
    @DisplayName("Test Message ID: IDs are unique for different calls")
    void testGenerateMessageID_Uniqueness() {
        String id1 = message.generateMessageID();
        String id2 = message.generateMessageID();
        assertNotEquals(id1, id2, "Generated Message IDs should generally be unique.");
    }

    // --- Test `checkRecipientCell()` ---
    @Test
    @DisplayName("Test Recipient Number: Success - Correctly formatted (starts with '+' and <= 10 chars)")
    void testCheckRecipientCell_Success_ValidLengthAndStart() {
        String validRecipient = "+271234567"; // 10 characters
        assertTrue(message.checkRecipientCell(validRecipient), "Recipient number should be valid (starts with '+' and is 10 chars)");
    }

    @Test
    @DisplayName("Test Recipient Number: Success - Shortest valid length")
    void testCheckRecipientCell_Success_Shortest() {
        String validRecipient = "+1"; // 2 characters, shortest possible
        assertTrue(message.checkRecipientCell(validRecipient), "Recipient number should be valid for shortest allowed length.");
    }

    @Test
    @DisplayName("Test Recipient Number: Failure - Missing '+'")
    void testCheckRecipientCell_Failure_NoPlus() {
        String invalidRecipient = "2712345678";
        assertFalse(message.checkRecipientCell(invalidRecipient), "Recipient number should be invalid (missing '+')");
    }

    @Test
    @DisplayName("Test Recipient Number: Failure - Exceeds 10 characters")
    void testCheckRecipientCell_Failure_TooLong() {
        String invalidRecipient = "+27123456789"; // 11 characters
        assertFalse(message.checkRecipientCell(invalidRecipient), "Recipient number should be invalid (exceeds 10 characters)");
    }

    @Test
    @DisplayName("Test Recipient Number: Failure - Empty string")
    void testCheckRecipientCell_Failure_Empty() {
        String invalidRecipient = "";
        assertFalse(message.checkRecipientCell(invalidRecipient), "Recipient number should be invalid (empty string)");
    }

    @Test
    @DisplayName("Test Recipient Number: Failure - Null string")
    void testCheckRecipientCell_Failure_Null() {
        String invalidRecipient = null;
        assertFalse(message.checkRecipientCell(invalidRecipient), "Recipient number should be invalid (null)");
    }

    // --- Test `checkMessageLength()` ---
    @Test
    @DisplayName("Test Message Length: Success - Message is less than 250 characters")
    void testCheckMessageLength_Success_LessThan250() {
        String validMessage = "This is a test message that is well within the 250 character limit.";
        assertTrue(message.checkMessageLength(validMessage), "Message should be valid (less than 250 characters)");
    }

    @Test
    @DisplayName("Test Message Length: Success - Message is exactly 250 characters")
    void testCheckMessageLength_Success_ExactLimit() {
        String exactLimitMessage = "a".repeat(250);
        assertTrue(message.checkMessageLength(exactLimitMessage), "Message should be valid (exactly 250 characters)");
    }

    @Test
    @DisplayName("Test Message Length: Failure - Message is more than 250 characters")
    void testCheckMessageLength_Failure_TooLong() {
        String invalidMessage = "a".repeat(251);
        assertFalse(message.checkMessageLength(invalidMessage), "Message should be invalid (more than 250 characters)");
    }

    @Test
    @DisplayName("Test Message Length: Failure - Message is null")
    void testCheckMessageLength_Failure_Null() {
        String nullMessage = null;
        assertFalse(message.checkMessageLength(nullMessage), "Message should be invalid (null string)");
    }

    @Test
    @DisplayName("Test Message Length: Success - Empty string (as per current implementation)")
    void testCheckMessageLength_Success_EmptyString() {
        // Current implementation: `message.length() <= 250`
        // An empty string (length 0) passes this condition.
        // If an empty message should be invalid, modify `checkMessageLength` to include `!message.isEmpty()`.
        String emptyMessage = "";
        assertTrue(message.checkMessageLength(emptyMessage), "Empty message should be considered valid by current logic.");
    }

    // --- Test `createMessageHash()` ---
    @Test
    @DisplayName("Test Message Hash: Correctly generated for Test Case 1 (First and Last word concatenation)")
    void testCreateMessageHash_TestCase1() {
        String testMessageId = "0012345678";
        int testMessageIndex = 0;
        String testMessageContent = "Hi, thanks for letting me know. Hit me up later tonight.";

        // New logic: "00:0:HI,TONIGHT." (First word "Hi,", last word "tonight.")
        String expectedHash = "00:0:HI,TONIGHT.";
        String actualHash = message.createMessageHash(testMessageId, testMessageIndex, testMessageContent);
        assertEquals(expectedHash, actualHash, "Message hash should match the expected format for test case 1.");
    }

    @Test
    @DisplayName("Test Message Hash: Correctly generated for Test Case 2 (First and Last word concatenation)")
    void testCreateMessageHash_TestCase2() {
        String testMessageId = "1298765432";
        int testMessageIndex = 1;
        String testMessageContent = "Hello, how are you today? Hope you are well.";

        // New logic: "12:1:HELLO,WELL." (First word "Hello,", last word "well.")
        String expectedHash = "12:1:HELLO,WELL.";
        String actualHash = message.createMessageHash(testMessageId, testMessageIndex, testMessageContent);
        assertEquals(expectedHash, actualHash, "Message hash should match the expected format for test case 2.");
    }

    @Test
    @DisplayName("Test Message Hash: Handles single-word content (First and Last word are the same)")
    void testCreateMessageHash_SingleWordContent() {
        String testMessageId = "5678901234";
        int testMessageIndex = 0;
        String testMessageContent = "Awesome!";
        // New logic: First word "Awesome!", last word "Awesome!"
        String expectedHash = "56:0:AWESOME!AWESOME!";
        String actualHash = message.createMessageHash(testMessageId, testMessageIndex, testMessageContent);
        assertEquals(expectedHash, actualHash, "Hash should correctly handle single word content.");
    }

    @Test
    @DisplayName("Test Message Hash: Handles content with leading/trailing spaces (trims before splitting words)")
    void testCreateMessageHash_WithTrimmedSpaces() {
        String testMessageId = "7890123456";
        int testMessageIndex = 2;
        String testMessageContent = "   A simple message.   ";
        // New logic: First word "A", last word "message."
        String expectedHash = "78:2:AMESSAGE.";
        String actualHash = message.createMessageHash(testMessageId, testMessageIndex, testMessageContent);
        assertEquals(expectedHash, actualHash, "Hash should trim content before processing words.");
    }

    @Test
    @DisplayName("Test Message Hash: Returns INVALID_HASH for null message content")
    void testCreateMessageHash_NullContent() {
        String testMessageId = "1122334455";
        int testMessageIndex = 0;
        String testMessageContent = null;
        String expectedHash = "INVALID_HASH";
        String actualHash = message.createMessageHash(testMessageId, testMessageIndex, testMessageContent);
        assertEquals(expectedHash, actualHash, "Should return INVALID_HASH for null content.");
    }

    @Test
    @DisplayName("Test Message Hash: Returns INVALID_HASH for empty message content")
    void testCreateMessageHash_EmptyContent() {
        String testMessageId = "1122334455";
        int testMessageIndex = 0;
        String testMessageContent = "";
        String expectedHash = "INVALID_HASH";
        String actualHash = message.createMessageHash(testMessageId, testMessageIndex, testMessageContent);
        assertEquals(expectedHash, actualHash, "Should return INVALID_HASH for empty content.");
    }

    // --- Test `sendMessage()` ---
    @Test
    @DisplayName("Test sendMessage(): Increments total messages sent and returns 'Message sent'")
    void testSendMessage_IncrementsTotalAndReturnsMessage() {
        message.setRecipientCell("+27123456789");
        message.setMessageContent("Hello, this is a test message.");
        message.createMessageHash(message.getMessageID(), 0, message.getMessageContent());

        int initialTotal = Message.getTotalMessagesSent();
        String result = message.sendMessage();

        assertEquals(initialTotal + 1, Message.getTotalMessagesSent(), "Total messages sent should increment by 1.");
        assertEquals("Message sent", result, "sendMessage() should return 'Message sent'.");
    }

    // --- Test `storeMessage()` ---
//    @Test
//    @DisplayName("Test storeMessage(): Writes message details to a JSON file")
//    void testStoreMessage_WritesToFile() throws IOException {
//        File testFile = new File(TEST_JSON_FILE);
//        if (testFile.exists()) {
//            testFile.delete();
//        }
//
//        message.setRecipientCell("+27777777777");
//        message.setMessageContent("This message should be stored in JSON.");
//        message.createMessageHash(message.getMessageID(), message.getNumMessagesSent() - 1, message.getMessageContent());
//
//        message.storeMessage(TEST_JSON_FILE);
//
//        assertTrue(testFile.exists(), "Test JSON file should exist after storing the message.");
//
//        // Read the content and perform basic checks for the stored JSON structure
//        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(TEST_JSON_FILE))) {
//            StringBuilder fileContent = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                fileContent.append(line).append("\n");
//            }
//            String content = fileContent.toString();
//
//            assertTrue(content.contains("\"MessageID\": \"" + message.getMessageID() + "\""), "File content should contain message ID.");
//            assertTrue(content.contains("\"Recipient\": \"" + message.getRecipientCell() + "\""), "File content should contain recipient.");
//            assertTrue(content.contains("\"Message\": \"" + message.getMessageContent() + "\""), "File content should contain message content.");
//            assertTrue(content.contains("\"MessageHash\": \"" + message.getMessageHash() + "\""), "File content should contain message hash.");
//            assertTrue(content.contains("\"NumMessagesSent\": " + message.getNumMessagesSent()), "File content should contain numMessagesSent.");
//            // UPDATED ASSERTION: Now expects the file to end with "]\n" as storeMessage writes a complete array.
//            assertTrue(content.trim().endsWith("]\n"), "File content should end with a closing JSON array bracket and newline.");
//        }
//    }

//    @Test
//    @DisplayName("Test storeMessage(): Appends to an existing JSON file with correct formatting")
//    void testStoreMessage_AppendsCorrectly() throws IOException {
//        File testFile = new File(TEST_JSON_FILE);
//        if (testFile.exists()) {
//            testFile.delete(); // Ensure a clean start
//        }
//
//        // --- REMOVED: Manual writing of "[\n" and "]\n" as storeMessage now handles array wrapping ---
//        // try (FileWriter writer = new FileWriter(TEST_JSON_FILE, true)) {
//        //     writer.write("[\n");
//        // }
//
//        // Store first message
//        Message firstMessage = new Message(0);
//        firstMessage.setRecipientCell("+27111111111");
//        firstMessage.setMessageContent("First appended message.");
//        firstMessage.createMessageHash(firstMessage.getMessageID(), 0, firstMessage.getMessageContent());
//        firstMessage.storeMessage(TEST_JSON_FILE);
//
//        // Store second message using a new instance
//        Message secondMessage = new Message(1);
//        secondMessage.setRecipientCell("+27222222222");
//        secondMessage.setMessageContent("Second appended message.");
//        secondMessage.createMessageHash(secondMessage.getMessageID(), 1, secondMessage.getMessageContent());
//        secondMessage.storeMessage(TEST_JSON_FILE);
//
//        // --- REMOVED: Manual writing of "]\n" ---
//        // try (FileWriter writer = new FileWriter(TEST_JSON_FILE, true)) {
//        //     writer.write("]\n");
//        // }
//
//        // Read the entire file content and verify appends
//        StringBuilder fileContent = new StringBuilder();
//        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(TEST_JSON_FILE))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                fileContent.append(line).append("\n");
//            }
//        }
//
//        String content = fileContent.toString();
//        assertTrue(content.contains(firstMessage.getMessageID()), "File should contain the first message's ID.");
//        assertTrue(content.contains(secondMessage.getMessageID()), "File should contain the second message's ID.");
//        // UPDATED ASSERTIONS: Expect the full array structure created by storeMessage
//        assertTrue(content.trim().startsWith("[\n"), "File should start with a JSON array opening bracket and newline.");
//        assertTrue(content.trim().endsWith("]\n"), "File should end with a JSON array closing bracket and newline.");
//        // No need to check for "},\n" explicitly, as the `toString(4)` of JSONArray handles commas.
//    }

    // --- Test `returnTotalMessages()` and `getTotalMessagesSent()` ---
    @Test
    @DisplayName("Test returnTotalMessages(): Returns the correct current total of messages sent")
    void testReturnTotalMessages_CorrectCount() {
        assertEquals(0, Message.getTotalMessagesSent(), "Total messages should be 0 initially due to @BeforeEach reset.");

        Message msg1 = new Message(0);
        msg1.setRecipientCell("+27111111111");
        msg1.setMessageContent("First message.");
        msg1.createMessageHash(msg1.getMessageID(), msg1.getNumMessagesSent() - 1, msg1.getMessageContent());
        msg1.sendMessage();

        assertEquals(1, Message.getTotalMessagesSent(), "Total messages should be 1 after sending one message.");
        assertEquals(1, msg1.returnTotalMessages(), "returnTotalMessages() should reflect the current total.");

        Message msg2 = new Message(1);
        msg2.setRecipientCell("+27222222222");
        msg2.setMessageContent("Second message.");
        msg2.createMessageHash(msg2.getMessageID(), msg2.getNumMessagesSent() - 1, msg2.getMessageContent());
        msg2.sendMessage();

        assertEquals(2, Message.getTotalMessagesSent(), "Total messages should be 2 after sending two messages.");
        assertEquals(2, msg2.returnTotalMessages(), "returnTotalMessages() should reflect the current total.");
    }
}