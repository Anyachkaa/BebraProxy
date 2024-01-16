package ru.justnanix.bebraproxy.network.packet.impl.client.play;

import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Collections;
import java.util.List;

public class ClientPlayerTryUseItemPacket extends Packet {
    private int hand;

    public ClientPlayerTryUseItemPacket() {

    }

    public ClientPlayerTryUseItemPacket(int hand) {
        this.hand = hand;
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeVarInt(hand);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        hand = in.readVarInt();
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Collections.singletonList(new Protocol(0x20, 340));
    }
}
