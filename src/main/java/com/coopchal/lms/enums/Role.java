package com.coopchal.lms.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ADMIN,
    FORMATEUR,
    APPRENANT;

    @JsonCreator
    public static Role fromString(String key) {
        return key == null ? null : Role.valueOf(key.toUpperCase());
    }
}
