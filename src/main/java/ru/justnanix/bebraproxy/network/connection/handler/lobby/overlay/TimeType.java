package ru.justnanix.bebraproxy.network.connection.handler.lobby.overlay;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TimeType {
    DAY(0, 6000),
    NIGHT(0, 18000),
    DEFAULT(0, 0);

    private final int age;
    private final int time;
}