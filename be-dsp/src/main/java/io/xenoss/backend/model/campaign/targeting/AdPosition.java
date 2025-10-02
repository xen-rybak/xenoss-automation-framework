package io.xenoss.backend.model.campaign.targeting;

import lombok.Getter;

@Getter
public enum AdPosition {
    UNKNOWN(0, "Unknown"),
    ABOVE_THE_FOLD(1, "Above the Fold"),
    VISIBLE_DEPENDING(2, "May or may not be initially visible depending on screen size/resolution"), // DEPRECATED
    BELOW_THE_FOLD(3, "Below the Fold"),
    HEADER(4, "Header"),
    FOOTER(5, "Footer"),
    SIDEBAR(6, "Sidebar"),
    FULL_SCREEN(7, "Full Screen");

    private final int openRtbIndex;
    private final String openRtbName;

    AdPosition(int openRtbIndex, String openRtbName) {
        this.openRtbIndex = openRtbIndex;
        this.openRtbName = openRtbName;
    }
}
