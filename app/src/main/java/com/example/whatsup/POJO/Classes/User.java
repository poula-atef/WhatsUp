package com.example.whatsup.POJO.Classes;

import java.util.List;

public class User {
    private String userName;
    private String birthDate;
    private String phoneNumber;
    private String userId;
    private String lastSeen;
    private boolean active;
    private List<Friend> friends;
    private String ImageUrl;

    public User() {
    }

    public User(String userName, String birthDate, String phoneNumber, boolean active, List<Friend> friends, String imageUrl, String userId, String lastSeen) {
        this.userName = userName;
        this.birthDate = birthDate;
        this.lastSeen = lastSeen;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.active = active;
        this.friends = friends;
        ImageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }
}
