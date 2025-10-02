package io.xenoss.backend.model.content.nativead.response;

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
    private List<NativeAssetResponseEntity> assets;
    private LinkEntity link;
    private List<String> imptrackers;
}
