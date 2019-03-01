package com.example.mobileliarsdice.Models;

public class Messages {
    String sender_id;
    String sender;
    String receiver_id;
    String receiver;
    String text;

    public Messages(){}

    public Messages(String sender_id, String sender, String receiver_id, String receiver, String text) {
        this.sender_id = sender_id;
        this.sender = sender;
        this.receiver_id = receiver_id;
        this.receiver = receiver;
        this.text = text;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
