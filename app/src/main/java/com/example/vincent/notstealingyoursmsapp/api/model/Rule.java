package com.example.vincent.notstealingyoursmsapp.api.model;

/**
 * Created by Vincent on 07/02/2017.
 */
public class Rule {
    private String phoneNumberFrom;
    private String phoneNumberTo;
    private String type;

    public Rule(String type, String phoneNumberTo, String phoneNumberFrom) {
        this.type = type;
        this.phoneNumberTo = phoneNumberTo;
        this.phoneNumberFrom = phoneNumberFrom;
    }

    public String getPhoneNumberFrom() {
        return phoneNumberFrom;
    }

    public String getPhoneNumberTo() {
        return phoneNumberTo;
    }

    public String getType() {
        return type;
    }
}
