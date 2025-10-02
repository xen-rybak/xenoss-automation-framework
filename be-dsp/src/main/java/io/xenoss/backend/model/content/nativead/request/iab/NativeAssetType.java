package io.xenoss.backend.model.content.nativead.request.iab;

import lombok.Getter;

import java.util.Arrays;

import static io.xenoss.backend.model.content.nativead.request.iab.NativeAssetMetaType.DATA;
import static io.xenoss.backend.model.content.nativead.request.iab.NativeAssetMetaType.IMAGE;

@Getter
public enum NativeAssetType {
    // Image types:
    ICON(1, IMAGE),        // Icon image. Optional. Max height: at least 50. Aspect ratio: 1:1
    MAIN(3, IMAGE),        // At least one of 2 size variants required:
                                // Small Variant: max height: at least 200; max width: at least 200, 267, or 382; aspect ratio: 1:1, 4:3, or 1.91:1
                                // Large Variant: max height: at least 627; max width: at least 627, 836, or 1198; aspect ratio: 1:1, 4:3, or 1.91:1

    // Data types:
    SPONSORED(1, DATA),   // Required. Max 25 or longer.
    DESC(2, DATA),        // Recommended. Max 140 or longer.
    RATING(3, DATA),      // Optional. 0-5 integer formatted as string.
    DOWNLOADS(5, DATA),   // Number formatted as string
    PRICE(6, DATA),       // Number formatted as string
    DISPLAYURL(11, DATA), // Text, no length restrictions
    CTATEXT(12, DATA);    // Text usually 15 chars max

    private final int value;
    private final NativeAssetMetaType type;

    NativeAssetType(int value, NativeAssetMetaType type) {
        this.value = value;
        this.type = type;
    }

    public static NativeAssetType getByTypeAndValue(int value, NativeAssetMetaType type) {
        return Arrays.stream(NativeAssetType.values())
                     .filter(asset -> asset.getValue() == value && asset.getType() == type)
                     .findFirst()
                     .orElseThrow();
    }
}
