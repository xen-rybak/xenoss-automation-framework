package io.xenoss.backend.model.bidding.request.bidswitch;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BidSwitchBidRequest {
    private List<BidSwitchImpEntity> imp;
    private String id;
}
