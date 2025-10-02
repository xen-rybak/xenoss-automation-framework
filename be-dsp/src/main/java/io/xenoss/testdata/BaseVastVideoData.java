package io.xenoss.testdata;

import io.xenoss.backend.model.content.vast.VastVideo;
import io.xenoss.backend.model.content.vast.LinearEntity;
import io.xenoss.backend.model.content.vast.MediaFileEntity;
import io.xenoss.backend.model.content.vast.IconEntity;
import io.xenoss.backend.model.content.vast.StaticResourceEntity;
import io.xenoss.backend.model.content.vast.AdEntity;
import io.xenoss.backend.model.content.vast.InLineEntity;
import io.xenoss.backend.model.content.vast.CreativeEntity;
import io.xenoss.backend.model.creative.MimeType;
import io.xenoss.utils.RandomUtils;

import java.util.Arrays;
import java.util.List;

public class BaseVastVideoData {
    public VastVideo getRandomVastVideo() {
        return getRandomVastVideo(null);
    }

    public VastVideo getRandomVastVideo(String aspectRatio) {
        var width = RandomUtils.randomNumber(10000, 20000);
        var height = RandomUtils.randomNumber(10000, 20000);

        if (aspectRatio != null) {
            int baseDimension = RandomUtils.randomNumber(10000, 20000);
            var widthCoefficient = Float.parseFloat(aspectRatio.split("x")[0]);
            var heightCoefficient = Float.parseFloat(aspectRatio.split("x")[1]);

            width = (int)(baseDimension * widthCoefficient);
            height = (int)(baseDimension * heightCoefficient);
        }

        return getRandomVastVideo(
                height,
                width,
                RandomUtils.randomItemFromList(
                                   Arrays.stream(MimeType.values())
                                         .filter(mime -> mime.toString()
                                                             .contains("video"))
                                         .toList())
                           .toString(),
                RandomUtils.randomNumber(10, 59),
                RandomUtils.randomNumber(10000, 50000));
    }

    public VastVideo getRandomVastVideo(Integer height, Integer width, String mimeType, Integer duration, Integer bitrate) {
        return getRandomVastVideo(
                RandomUtils.randomUrl() + "/video.mp4",
                RandomUtils.randomUrl() + "/thumbnail.png",
                height, width, mimeType, duration, bitrate);
    }

    public VastVideo getRandomVastVideo(String url, String previewUrl, Integer height, Integer width, String mimeType, Integer duration, Integer bitrate) {
        var durationText = String.format(duration < 10
                ? "00:00:0%s"
                : "00:00:%s", duration);

        var linearEntity = LinearEntity.builder()
                                       .duration(durationText)
                                       .mediaFiles(List.of(
                                               MediaFileEntity.builder()
                                                              .width(width.toString())
                                                              .height(height.toString())
                                                              .type(mimeType)
                                                              .bitrate(bitrate.toString())
                                                              .delivery("progressive")
                                                              .value(url)
                                                              .build()))
                                       .icons(List.of(IconEntity.builder()
                                                                .program("thumbnail")
                                                                .height(height.toString())
                                                                .width(width.toString())
                                                                .xPosition("left")
                                                                .yPosition("top")
                                                                .staticResource(StaticResourceEntity.builder()
                                                                                                    .creativeType(MimeType.PNG.toString())
                                                                                                    .value(previewUrl)
                                                                                                    .build())
                                                                .build()))
                                       .build();

        return VastVideo.builder()
                        .ad(AdEntity.builder()
                                    .id(null)
                                    .sequence("1")
                                    .inLine(InLineEntity.builder()
                                                        .adTitle("Title")
                                                        .creatives(List.of(
                                                                CreativeEntity.builder()
                                                                              .id(RandomUtils.randomUuid())
                                                                              .sequence("1")
                                                                              .linear(linearEntity)
                                                                              .build()))
                                                        .build())
                                    .adType("video")
                                    .build())
                        .version("4.3")
                        .build();
    }
}
