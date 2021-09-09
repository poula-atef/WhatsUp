package com.example.whatsup.POJO.Classes;

public class PushedNotification {
    private NotificationData data;
    private String to;

    public PushedNotification() {
    }

    public PushedNotification(NotificationData data, String to) {
        this.data = data;
        this.to = to;
    }

    public NotificationData getData() {
        return data;
    }

    public void setData(NotificationData data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
