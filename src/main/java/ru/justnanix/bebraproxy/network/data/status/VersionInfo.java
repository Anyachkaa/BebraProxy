package ru.justnanix.bebraproxy.network.data.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VersionInfo {
    private final String versionName;
    private final int protocolVersion;
}