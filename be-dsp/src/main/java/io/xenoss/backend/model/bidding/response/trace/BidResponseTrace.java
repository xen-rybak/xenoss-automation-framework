package io.xenoss.backend.model.bidding.response.trace;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BidResponseTrace {
    private Map<String, List<String>> targetingTree;
    private List<String> found;
    private DecisionStagesEntity decisionStages;
}
