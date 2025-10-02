package io.xenoss.backend.model.content.html;

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
public class ImgEntity {
    @JacksonXmlProperty(isAttribute = true)
    public String width;
    @JacksonXmlProperty(isAttribute = true)
    public String height;
    @JacksonXmlProperty(isAttribute = true)
    public String src;
}
