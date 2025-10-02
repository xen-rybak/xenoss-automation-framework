package io.xenoss.backend.model.content.nativead.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class NativeAssetResponseEntity {
    private Integer id;
    private NativeTitleResponseEntity title;
    private NativeImgResponseEntity img;
    private NativeVideoResponseEntity video;
    private NativeDataResponseEntity data;
}
