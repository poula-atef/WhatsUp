package com.example.whatsup.POJO.Classes;

public class Message {
    private String senderId;
    private String receiverId;
    private String Message;
    private String senderNumber;
    private String senderImg;
    private String senderName;
    private boolean img;
    private String time;
    private String imageUrl;
    private int seen;

    public Message() {
    }

    public Message(String senderId, String receiverId, String message, boolean img, String time,
                   String imageUrl, int seen,String senderNumber,String senderImg,String senderName) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        Message = message;
        this.img = img;
        this.time = time;
        this.imageUrl = imageUrl;
        this.seen = seen;
        this.senderNumber = senderNumber;
        this.senderImg = senderImg;
        this.senderName = senderName;
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

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isImg() {
        return img;
    }

    public void setImg(boolean img) {
        this.img = img;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getSenderImg() {
        return senderImg;
    }

    public void setSenderImg(String senderImg) {
        this.senderImg = senderImg;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
