package com.docsapp.test.chatbot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Model Class for Server Response
 */
public class Response {

    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("message")
    @Expose
    private Message message;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
