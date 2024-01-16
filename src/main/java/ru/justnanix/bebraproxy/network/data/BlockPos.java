package ru.justnanix.bebraproxy.network.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class BlockPos {
    private int x;
    private int y;
    private int z;
}
