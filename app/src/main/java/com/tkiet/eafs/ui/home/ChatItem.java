package com.tkiet.eafs.ui.home;

public class ChatItem {
    public static final int TYPE_DATE = 0;
    public static final int TYPE_MESSAGE = 1;

    private int type;
    private Message message;
    private String dateHeader; // For date section header

    // Constructor for message
    public ChatItem(Message message) {
        this.type = TYPE_MESSAGE;
        this.message = message;
    }

    // Constructor for date header
    public ChatItem(String dateHeader) {
        this.type = TYPE_DATE;
        this.dateHeader = dateHeader;
    }

    public int getType() {
        return type;
    }

    public Message getMessage() {
        return message;
    }

    public String getDateHeader() {
        return dateHeader;
    }
}

