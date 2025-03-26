package com.natixis.commerce.utils;

public enum MessageStatus {

    USER_INVALID("User or password is invalid!"),
    RECORD_NOT_FOUND("Record not found!");

    private final String description;

    MessageStatus(String s) {
        this.description = s;
    }

    public String getDescription() {
        return description;
    }
}
