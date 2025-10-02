package io.xenoss.backend.model.content.vast;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum VastEvent {
    @JsonProperty("skip")
    SKIP("skip"),
    @JsonProperty("firstQuartile")
    FIRST_QUARTILE("firstQuartile"),
    @JsonProperty("midpoint")
    MIDPOINT("midpoint"),
    @JsonProperty("thirdQuartile")
    THIRD_QUARTILE("thirdQuartile"),
    @JsonProperty("complete")
    COMPLETE("complete");

    private final String eventName;

    VastEvent(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public String toString() {
        return this.eventName;
    }
}
