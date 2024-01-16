package ru.justnanix.bebraproxy.network.packet.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.utils.network.PacketUtil;

import java.util.List;

@Getter
@AllArgsConstructor
public class CustomPacket extends Packet {
    private final int customPacketID;
    private byte[] customData;

    public CustomPacket(int id) {
        this.customPacketID = id;
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeBytes(customData);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.customData = new byte[in.readableBytes()];
        in.readBytes(customData);
    }

    public PacketBuffer getPacketBuffer() {
        PacketBuffer empty = PacketUtil.createEmptyPacketBuffer();
        empty.writeBytes(customData);

        return empty;
    }

    @Override
    public List<Protocol> getProtocolList() {
        return null;
    }
}