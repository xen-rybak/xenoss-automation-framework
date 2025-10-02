package io.xenoss.backend.model.bidding.response.trace;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class DecisionStagesEntity extends ArrayList<DecisionStageEntity> {
    public DecisionStagesEntity(Collection<DecisionStageEntity> collection) {
        super(collection);
    }
}
