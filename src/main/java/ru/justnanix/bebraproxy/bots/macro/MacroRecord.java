package ru.justnanix.bebraproxy.bots.macro;

import lombok.Data;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.List;

@Data
public class MacroRecord {
    private PosChange posChange;
    private final List<Packet> packets;

    @Data
    public static class PosChange {
        private final double xChange, yChange, zChange;
    }
}
