package com.tkiet.eafs.ui.dashboard;

public class ChatUser {
    private String userId;
    private String userName;  // For displaying the name
    private String userPhotoUrl;  // Optional: for displaying profile picture

    public ChatUser() {}  // Required for Firebase

    public ChatUser(String userId, String userName, String userPhotoUrl) {
        this.userId = userId;
        this.userName = userName;
        this.userPhotoUrl = userPhotoUrl;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserPhotoUrl() { return userPhotoUrl; }
}
