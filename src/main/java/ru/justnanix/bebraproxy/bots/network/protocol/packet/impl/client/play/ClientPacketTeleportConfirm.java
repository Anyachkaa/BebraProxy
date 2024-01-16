package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.client.play;

import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

public class ClientPacketTeleportConfirm extends Packet {
    private int teleportID;

    public ClientPacketTeleportConfirm() {

    }

    public ClientPacketTeleportConfirm(int teleportID) {
        this.teleportID = teleportID;
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        teleportID = in.readVarInt();
    }

    @Override
    public void write(PacketBuffer out, int protocol) {
        out.writeVarInt(teleportID);
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x00, 340));
    }
}
