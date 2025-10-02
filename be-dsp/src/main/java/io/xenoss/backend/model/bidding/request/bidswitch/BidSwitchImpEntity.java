package io.xenoss.backend.model.bidding.request.bidswitch;

import com.google.gson.annotations.SerializedName;
import io.xenoss.backend.model.content.nativead.request.bidswitch.BidSwitchNativeSubSectionEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BidSwitchImpEntity {
    @SerializedName("native")
    private BidSwitchNativeSubSectionEntity nativeAd;
}
