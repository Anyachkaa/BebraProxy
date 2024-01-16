package ru.justnanix.bebraproxy.network.data.chunk;

import lombok.Data;

@Data
public class NetworkChunkData {
    private final int mask;
    private final boolean fullChunk;
    private final boolean sky;
    private final byte[] data;
}