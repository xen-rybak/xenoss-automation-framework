package io.xenoss.backend.model.content.vast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class VideoClicksEntity {
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "ClickThrough")
    private String clickThrough;
    @JacksonXmlCData
    @JacksonXmlElementWrapper(useWrapping=false)
    @JacksonXmlProperty(localName = "ClickTracking")
    private List<String> clickTracking;
}
