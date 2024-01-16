package ru.justnanix.bebraproxy.bots.network.protocol.data.chunk;

import lombok.Data;

@Data
public class ParsedChunkData {
    private final Chunk[] chunks;
    private final byte[] biomes;
}