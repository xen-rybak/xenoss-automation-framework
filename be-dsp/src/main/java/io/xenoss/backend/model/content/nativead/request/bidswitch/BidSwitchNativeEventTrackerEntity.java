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
public class BidSwitchNativeEventTrackerEntity {
    private Integer event;
    private List<Integer> methods;
}
