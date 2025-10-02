package io.xenoss.backend.model.bidding.response.trace;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class DecisionStageEntity {
    private String type;
    private String description;
    private DecisionStageStatus status;
    private SelectionEntity selection;
}
