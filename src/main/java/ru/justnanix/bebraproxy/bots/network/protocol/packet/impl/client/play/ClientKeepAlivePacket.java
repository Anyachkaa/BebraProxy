package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientKeepAlivePacket extends Packet {
    private long time;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        if (protocol >= 340) {
            out.writeLong(this.time);
        } else {
            out.writeVarInt((int) this.time);
        }
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        if (protocol >= 340) {
            this.time = in.readLong();
        } else {
            this.time = in.readVarInt();
        }
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x00, 47), new Protocol(0x0B, 107, 108, 109, 110, 210, 315, 316, 338, 340), new Protocol(0x0C, 335));
    }
}