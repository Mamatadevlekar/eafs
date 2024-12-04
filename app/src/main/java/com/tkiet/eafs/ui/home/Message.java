package com.tkiet.eafs.ui.home;

public class Message {
    private String senderId;
    private String receiverId;
    private String messageText;
    private long timestamp;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Message(String senderId, String receiverId, String messageText, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getMessageText() { return messageText; }
    public long getTimestamp() { return timestamp; }
}
