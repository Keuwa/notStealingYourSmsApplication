package com.example.vincent.notstealingyoursmsapp.api.model;

/**
 * Created by Vincent on 07/02/2017.
 */
public class MySms {
    private String phoneNumberFrom;
    private String phoneNumberTo;
    private String date;
    private String message;

    public MySms(String phoneNumberFrom, String phoneNumberTo, String date, String message) {
        this.phoneNumberFrom = phoneNumberFrom;
        this.phoneNumberTo = phoneNumberTo;
        this.date = date;
        this.message = message;
    }

    public String getPhoneNumberFrom() {
        return phoneNumberFrom;
    }

    public String getPhoneNumberTo() {
        return phoneNumberTo;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }
}
