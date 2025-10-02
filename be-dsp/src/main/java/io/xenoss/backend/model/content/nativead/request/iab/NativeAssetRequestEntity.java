package io.xenoss.backend.model.content.nativead.request.iab;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class NativeAssetRequestEntity {
    private Integer id;
    private Integer required;
    private NativeTitleRequestEntity title;
    private NativeImgRequestEntity img;
    private NativeVideoRequestEntity video;
    private NativeDataRequestEntity data;
}
