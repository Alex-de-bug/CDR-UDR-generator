package com.alwx.call.model;

import lombok.Getter;

@Getter
public enum CallType {
    INCOMING("01"),
    OUTCOMING("02");

    private final String code;

    CallType(String code) {
        this.code = code;
    }
}
