package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.client.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientStatusPingPacket extends Packet {
    private long time;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeLong(this.time);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.time = in.readLong();
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Collections.singletonList(new Protocol(0x01, 47, 107, 108, 109, 110, 210, 315, 316, 335, 338, 340));
    }
}