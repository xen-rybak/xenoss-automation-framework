package io.xenoss.backend.model.campaign.targeting;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("MALE"),
    FEMALE("FEMALE"),
    UNKNOWN("UNKNOWN");

    private final String gender;

    Gender(String gender) {
        this.gender = gender;
    }
}
