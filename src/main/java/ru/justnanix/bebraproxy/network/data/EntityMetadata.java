package ru.justnanix.bebraproxy.network.data;

import lombok.Data;

@Data
public class EntityMetadata {
    private final int id;
    private final MetadataType type;
    private final Object value;
}
