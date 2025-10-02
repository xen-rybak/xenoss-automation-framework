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
public class InLineEntity {
    @JacksonXmlCData
    @JacksonXmlElementWrapper(useWrapping=false)
    @JacksonXmlProperty(localName = "Impression")
    private List<String> impression;
    @JacksonXmlProperty(localName = "AdTitle")
    private String adTitle;
    @JacksonXmlElementWrapper(localName = "Creatives")
    @JacksonXmlProperty(localName = "Creative")
    private List<CreativeEntity> creatives;
}
