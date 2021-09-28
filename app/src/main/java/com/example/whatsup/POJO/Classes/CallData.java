package com.example.whatsup.POJO.Classes;

public class CallData {
    private String senderName;
    private String senderNumber;
    private String senderToken;
    private String state;
    private String callType;

    public CallData() {
    }

    public CallData(String senderName, String senderNumber, String callType, String state, String senderToken) {
        this.senderName = senderName;
        this.senderNumber = senderNumber;
        this.callType = callType;
        this.state = state;
        this.senderToken = senderToken;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSenderToken() {
        return senderToken;
    }

    public void setSenderToken(String senderToken) {
        this.senderToken = senderToken;
    }
}
