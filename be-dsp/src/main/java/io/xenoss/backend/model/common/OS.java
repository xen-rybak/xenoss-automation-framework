package io.xenoss.backend.model.common;

import io.xenoss.utils.RandomUtils;
import lombok.Getter;

import java.util.List;

@Getter
public enum OS {
    IOS("iOS"),
    ANDROID("Android"),
    WINDOWS(RandomUtils.randomItemFromList(List.of("Windows", "windows", "Windows 7", "Windows 11"))),
    MAC_OS(RandomUtils.randomItemFromList(List.of("MacOS", "Mac OS", "OS X"))),
    LINUX(RandomUtils.randomItemFromList(List.of("Linux", "Ubuntu"))),
    OTHER(RandomUtils.randomItemFromList(List.of("OS/2", "BeOS", "AmigaOS", "Windows Phone", "Harmony", "TempleOS")));

    private final String name;

    OS(String name) {
        this.name = name;
    }
}
