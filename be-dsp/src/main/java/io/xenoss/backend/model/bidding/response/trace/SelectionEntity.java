package io.xenoss.backend.model.bidding.response.trace;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SelectionEntity {
    private String organizationId;
    private String account;
    private String campaign;
    private String lineitem;
    private String creative;
    private String assets;
}
