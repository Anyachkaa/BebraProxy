package ru.justnanix.bebraproxy.player.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Plan {
    ADMIN("&cАдмин", 1000, 1),
    MAX("&cМаксимальный", 600, 1),
    ADVANCED("&aПродвинутый", 300, 2),
    BASIC("&7Базовый", 100, 3),
    FREE("&aБесплатный", 50, 3);

    private final String prefix;
    private final int maxBots;
    private final int delayCMD;
}