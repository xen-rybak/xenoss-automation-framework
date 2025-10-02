package io.xenoss.backend.model.content.vast;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class LinearEntity {
    @JacksonXmlProperty(localName = "Duration")
    private String duration;
    @JacksonXmlElementWrapper(localName = "MediaFiles")
    @JacksonXmlProperty(localName = "MediaFile")
    private List<MediaFileEntity> mediaFiles;
    @JacksonXmlElementWrapper(localName = "Icons")
    @JacksonXmlProperty(localName = "Icon")
    private List<IconEntity> icons;
    @JacksonXmlProperty(localName = "VideoClicks")
    private VideoClicksEntity videoClicks;
    @JacksonXmlElementWrapper(localName = "TrackingEvents")
    @JacksonXmlProperty(localName = "Tracking")
    private List<TrackingEntity> trackingEvents;
}
