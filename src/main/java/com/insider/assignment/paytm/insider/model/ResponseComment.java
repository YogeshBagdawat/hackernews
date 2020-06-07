package com.insider.assignment.paytm.insider.model;

public class ResponseComment {

    private String text;
    private String UserHNHandle;
    private long age;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserHNHandle() {
        return UserHNHandle;
    }

    public void setUserHNHandle(String userHNHandle) {
        UserHNHandle = userHNHandle;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }
}
