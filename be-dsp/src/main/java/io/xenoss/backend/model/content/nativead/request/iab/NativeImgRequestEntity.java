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
public class NativeImgRequestEntity {
    private Integer type;
    private Integer wmin;
    private Integer hmin;
    private Integer h;
    private Integer w;
    private List<String> mimes;
}
