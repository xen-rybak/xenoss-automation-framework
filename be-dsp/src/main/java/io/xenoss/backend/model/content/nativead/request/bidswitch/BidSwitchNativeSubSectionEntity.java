package io.xenoss.backend.model.content.nativead.request.bidswitch;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BidSwitchNativeSubSectionEntity {
    private BidSwitchNativeExtEntity ext;
    private BidSwitchNativeRequestEntity request;
    private List<Integer> battr;
    private List<Integer> api;
}
