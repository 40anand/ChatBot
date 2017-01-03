package com.docsapp.test.chatbot.model;

/**
 * Model class corresponding to one Chat message sent by user or Server
 */
public class ChatMessage {

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public enum Sender {
        SENDER_USER,
        SENDER_BOT
    }

    private String message;
    private Long timeStamp;
    private Sender sender;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
