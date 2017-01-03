package com.docsapp.test.chatbot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Model Class for Server Response
 */
public class Message {

    @SerializedName("chatBotName")
    @Expose
    private String chatBotName;
    @SerializedName("chatBotID")
    @Expose
    private Integer chatBotID;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("emotion")
    @Expose
    private Object emotion;

    public String getChatBotName() {
        return chatBotName;
    }

    public void setChatBotName(String chatBotName) {
        this.chatBotName = chatBotName;
    }

    public Integer getChatBotID() {
        return chatBotID;
    }

    public void setChatBotID(Integer chatBotID) {
        this.chatBotID = chatBotID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getEmotion() {
        return emotion;
    }

    public void setEmotion(Object emotion) {
        this.emotion = emotion;
    }

}