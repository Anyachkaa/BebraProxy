package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.play;

import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

public class ServerPacketChunkUnload extends Packet {
    private int x, z;

    public ServerPacketChunkUnload() {
    }

    @Override
    public void read(PacketBuffer in, int protocol) {
        x = in.readInt();
        z = in.readInt();
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeInt(x);
        out.writeInt(z);
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x1D, 340));
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
}
