package io.xenoss.backend.model.content.vast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@JacksonXmlRootElement(localName = "VAST")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class VastVideo {
    @JacksonXmlProperty(localName = "Ad")
    private AdEntity ad;
    @JacksonXmlProperty(isAttribute = true)
    private String version;
}
