package com.example.whatsup.POJO.Classes;

public class Friend {
    private String senderId;
    private String userName;
    private String profileImageUrl;
    private int seen;
    private String lastMessage;
    private String lastDate;

    public Friend() {
    }

    public Friend(String senderId, String userName, String profileImageUrl, int seen, String lastMessage, String lastDate) {
        this.senderId = senderId;
        this.userName = userName;
        this.profileImageUrl = profileImageUrl;
        this.seen = seen;
        this.lastMessage = lastMessage;
        this.lastDate = lastDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
