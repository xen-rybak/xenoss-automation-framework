package io.xenoss.backend.model.creative;

import lombok.Getter;

@Getter
public enum OpenRtbPlacementType {
    BANNER(1),
    VIDEO(2),
    NATIVE(3),
    AUDIO(4);

    private final int value;

    OpenRtbPlacementType(int value) {
        this.value = value;
    }

}
