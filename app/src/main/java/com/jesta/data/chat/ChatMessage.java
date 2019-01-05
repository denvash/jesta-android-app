package com.jesta.data.chat;

import com.jesta.data.User;

import java.util.Comparator;

public class ChatMessage {
    String id;
    String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    User sender;
    String createdAt;

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

    public static class SortByDate implements Comparator<ChatMessage>
    {
        @Override
        public int compare(ChatMessage o1, ChatMessage o2) {
            return (int)(Long.parseLong(o1.getCreatedAt()) - Long.parseLong(o2.getCreatedAt()));
        }
    }
}