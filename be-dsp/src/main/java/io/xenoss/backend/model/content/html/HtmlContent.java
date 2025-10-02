package io.xenoss.backend.model.content.html;

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
@JacksonXmlRootElement(localName = "a")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HtmlContent {
    @JacksonXmlProperty(localName = "img")
    private ImgEntity img;
    @JacksonXmlProperty(isAttribute = true)
    private String href;
}
