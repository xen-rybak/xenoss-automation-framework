package io.xenoss.backend.model.bidding.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BidResponse {
    private List<SeatbidEntity> seatbid;
    private String bidid;
    private String cur;
    private String id;
    private BidResponseExtEntity ext;
}
