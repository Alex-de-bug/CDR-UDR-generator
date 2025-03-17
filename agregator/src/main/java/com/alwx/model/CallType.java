package com.alwx.model;

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
