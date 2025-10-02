package io.xenoss.backend.model.content.nativead.request.bidswitch;

import io.xenoss.backend.model.content.nativead.request.iab.NativeAssetRequestEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BidSwitchNativeRequestEntity {
    private String ver;
    private Integer layout;
    private Integer adunit;
    private Integer plcmttype;
    private Integer plcmtcnt;
    private Integer seq;
    private Integer privacy;
    private Integer context;
    private Integer contextsubtype;
    private List<NativeAssetRequestEntity> assets;
    private List<BidSwitchNativeEventTrackerEntity> eventtrackers;
}
