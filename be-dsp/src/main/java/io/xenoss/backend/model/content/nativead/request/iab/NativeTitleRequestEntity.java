package io.xenoss.backend.model.content.nativead.request.iab;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class NativeTitleRequestEntity {
    private Integer len;
}
