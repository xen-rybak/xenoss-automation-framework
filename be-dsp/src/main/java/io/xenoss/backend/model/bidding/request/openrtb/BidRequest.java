package io.xenoss.backend.model.bidding.request.openrtb;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BidRequest {
    private List<ImpEntity> imp;
    private String id;
}
