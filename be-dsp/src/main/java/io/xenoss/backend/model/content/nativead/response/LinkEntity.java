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
public class LinkEntity {
    private String url;
    private String fallback;
    private List<String> clicktrackers;
}
