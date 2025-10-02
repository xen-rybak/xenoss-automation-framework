package io.xenoss.backend.model.content.nativead.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class NativeAdResponseEntity {
    @SerializedName("native")
    private NativeSubSectionEntity nativeAd;
}
