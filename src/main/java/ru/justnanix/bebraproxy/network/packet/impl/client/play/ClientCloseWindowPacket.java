package ru.justnanix.bebraproxy.network.packet.impl.client.play;

import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ClientCloseWindowPacket extends Packet {
    private int windowId;

    @Override
    public void write(PacketBuffer out, int protocol) throws IOException {
        out.writeByte(this.windowId);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws IOException {
        this.windowId = in.readByte();
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x0D, 47), new Protocol(0x08, 340));
    }
}