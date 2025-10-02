package io.xenoss.backend.model.bidding.request.openrtb;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ImpEntity {
    @SerializedName("native")
    private NativeAdEntity nativeAd;
}
