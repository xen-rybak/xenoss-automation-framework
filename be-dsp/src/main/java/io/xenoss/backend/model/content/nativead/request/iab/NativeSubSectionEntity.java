package io.xenoss.backend.model.content.nativead.request.iab;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class NativeSubSectionEntity {
    private String ver;
    private Integer layout;
    private List<NativeAssetRequestEntity> assets;
}
