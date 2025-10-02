package io.xenoss.backend.model.common;

import lombok.Getter;

import java.util.List;

@Getter
public enum DeviceType {
    SMARTPHONES(List.of(1, 4)),
    TABLETS(List.of(1, 5)),
    CTV(List.of(3, 7)),
    DESKTOP(List.of(2));

    private final List<Integer> indexes;

    DeviceType(List<Integer>  indexes) {
        this.indexes = indexes;
    }
}
