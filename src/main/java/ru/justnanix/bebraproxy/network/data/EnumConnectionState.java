package ru.justnanix.bebraproxy.network.data;

import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.network.packet.PacketDirection;

import java.util.ArrayList;
import java.util.List;

public enum EnumConnectionState {
    HANDSHAKE, LOGIN, PLAY, STATUS;

    private final List<Packet> clientPackets, serverPackets;

    EnumConnectionState() {
        this.clientPackets = new ArrayList<>();
        this.serverPackets = new ArrayList<>();
    }

    public List<Packet> getPacketsByDirection(PacketDirection direction) {
        switch (direction) {
            case SERVERBOUND:
                return clientPackets;
            case CLIENTBOUND:
                return serverPackets;
        }
        return null;
    }
}