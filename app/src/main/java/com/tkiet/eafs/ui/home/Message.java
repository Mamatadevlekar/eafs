package com.tkiet.eafs.ui.home;
public class Message {
    private String senderId;
    private String receiverId;
    private String messageText;
    private long timestamp;

    // Required empty constructor for Firebase
    public Message() {}

    public Message(String senderId, String receiverId, String messageText, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    // Getters
    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessageText() {
        return messageText;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
