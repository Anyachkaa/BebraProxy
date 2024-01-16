package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.client.status;

import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Collections;
import java.util.List;

public class ClientStatusRequestPacket extends Packet {
    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Collections.singletonList(new Protocol(0x00, 47, 107, 108, 109, 110, 210, 315, 316, 335, 338, 340));
    }
}