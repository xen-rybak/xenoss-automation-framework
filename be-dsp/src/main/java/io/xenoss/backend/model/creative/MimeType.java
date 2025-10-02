package io.xenoss.backend.model.creative;

import lombok.Getter;

@Getter
public enum MimeType {
    JPEG("image/jpeg"),
    PNG("image/png"),
    GIF("image/gif"),
    MP4("video/mp4"),
    WEBM("video/webm"),
    HTML("text/html"),
    JAVASCRIPT("application/javascript");

    private final String name;

    MimeType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
