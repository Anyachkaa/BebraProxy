package ru.justnanix.bebraproxy.bots.mirror;

import lombok.Data;
import ru.justnanix.bebraproxy.network.data.Position;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.List;

@Data
public class MirrorRecord {
    private final Position recordPosition;
    private final List<Packet> recordPackets;
}
