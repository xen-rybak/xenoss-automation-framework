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
public class BidSwitchTripleFitEntity {
    private List<Integer> formats;
}
