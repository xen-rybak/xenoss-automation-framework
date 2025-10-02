package io.xenoss.backend.model.campaign.targeting;

import lombok.Getter;

@Getter
public enum ConnectionTypeTarget {
    CELL_2G(4, "Cellular Network – 2G"),
    CELL_3G(5, "Cellular Network – 3G"),
    CELL_4G(6, "Cellular Network - 4G"),
    CELL_5G(7, "Cellular Network - 5G"),
    CELL_UNKNOWN(3, "Cellular Network – Unknown Generation"),
    WIFI(2, "WIFI"),
    ETHERNET(1, "Ethernet; Wired Connection"),
    UNKNOWN(0, "Unknown");

    private final int openRtbIndex;
    private final String openRtbName;

    ConnectionTypeTarget(int openRtbIndex, String openRtbName) {
        this.openRtbIndex = openRtbIndex;
        this.openRtbName = openRtbName;
    }
}
