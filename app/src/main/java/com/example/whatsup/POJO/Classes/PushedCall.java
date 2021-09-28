package com.example.whatsup.POJO.Classes;

public class PushedCall {
    private CallData data;
    private String to;

    public PushedCall() {
    }

    public PushedCall(CallData data, String to) {
        this.data = data;
        this.to = to;
    }

    public CallData getData() {
        return data;
    }

    public void setData(CallData data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
