package com.example.whatsup.POJO.Classes;

import java.util.HashMap;
import java.util.List;

public class User {
    private String userName;
    private String birthDate;
    private String phoneNumber;
    private String userId;
    private String lastSeen;
    private boolean active;
    private HashMap<String,?> friends;
    private String ImageUrl;
    private String token;

    public User() {
    }

    public User(String userName, String birthDate, String phoneNumber, boolean active, HashMap<String,?> friends, String imageUrl, String userId, String lastSeen, String token) {
        this.userName = userName;
        this.birthDate = birthDate;
        this.lastSeen = lastSeen;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.active = active;
        this.friends = friends;
        this.ImageUrl = imageUrl;
        this.token = token;
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

    public HashMap<String,?> getFriends() {
        return friends;
    }

    public void setFriends(HashMap<String,?> friends) {
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
