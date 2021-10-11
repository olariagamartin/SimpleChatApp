package com.themarto.mychatapp.data.domain;

public class MessageModel {

    private String message;
    private String senderId;
    private String receiverId;
    private String chatRoom;
    private long timestamp;

    public MessageModel() { }

    public MessageModel(String message, String senderId, String receiverId, String chatRoom, long timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.chatRoom = chatRoom;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(String chatRoom) {
        this.chatRoom = chatRoom;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
