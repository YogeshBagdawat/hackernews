package com.insider.assignment.paytm.insider.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseUser {
    @JsonProperty("id")
    private String HNHandle;
    @JsonProperty("created")
    private long age;

    public String getHNHandle() {
        return HNHandle;
    }

    public void setHNHandle(String HNHandle) {
        this.HNHandle = HNHandle;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }
}
