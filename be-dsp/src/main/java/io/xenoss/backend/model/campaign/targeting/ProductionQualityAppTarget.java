package io.xenoss.backend.model.campaign.targeting;

import lombok.Getter;

@Getter
public enum ProductionQualityAppTarget {
    UNKNOWN(0, "Unknown"),
    PROFESSIONALLY_PRODUCED(1, "Professionally Produced"),
    PROSUMER(2, "Prosumer"),
    USER_GENERATED(3, "User Generated (UGC)");

    private final int openRtbIndex;
    private final String openRtbName;

    ProductionQualityAppTarget(int openRtbIndex, String openRtbName) {
        this.openRtbIndex = openRtbIndex;
        this.openRtbName = openRtbName;
    }
}
