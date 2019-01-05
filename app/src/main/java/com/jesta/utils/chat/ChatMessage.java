package com.jesta.utils.chat;

import com.jesta.data.User;

public class ChatMessage {
    String id;
    String message;
    User sender;
    String createdAt;

    public ChatMessage(String message, User sender) {
        this.message = message;
        this.sender = sender;
    }

    public ChatMessage(String id, String message, User sender, String createdAt) {
        this.id = id;
        this.message = message;
        this.sender = sender;
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ChatMessage)) {
            return false;
        }
        ChatMessage msg = (ChatMessage)o;
        return msg.id == this.id;
    }
}