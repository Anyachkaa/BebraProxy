package ru.justnanix.bebraproxy.network.data;

import lombok.Data;

@Data
public class Effect {
    private final int effectReason;
    private final float value;
}