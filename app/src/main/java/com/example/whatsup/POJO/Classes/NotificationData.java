package com.example.whatsup.POJO.Classes;

public class NotificationData {
    private String title;
    private String message;
    private long senderNumber;
    private String imgUrl;

    public NotificationData() {
    }

    public NotificationData(String title, String message, String imgUrl,long senderNumber) {
        this.title = title;
        this.message = message;
        this.imgUrl = imgUrl;
        this.senderNumber = senderNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public long getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(long senderNumber) {
        this.senderNumber = senderNumber;
    }
}
