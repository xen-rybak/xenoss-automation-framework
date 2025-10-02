package io.xenoss.backend.model.campaign.targeting;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class TargetingOptionEntity {
    private List<String> whitelist;
    private List<String> blacklist;
    private Boolean whitelistand;
}
