package io.xenoss.backend.model.content.vast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IconEntity {
    @JacksonXmlProperty(isAttribute = true)
    private String program;
    @JacksonXmlProperty(localName = "xPosition", isAttribute = true)
    private String xPosition;
    @JacksonXmlProperty(localName = "yPosition", isAttribute = true)
    private String yPosition;
    @JacksonXmlProperty(isAttribute = true)
    private String width;
    @JacksonXmlProperty(isAttribute = true)
    private String height;
    @JacksonXmlProperty(localName = "StaticResource")
    private StaticResourceEntity staticResource;
}
