package io.xenoss.backend.model.content.nativead.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class NativeImgResponseEntity {
    private String url;
    private Integer h;
    private Integer w;
}
