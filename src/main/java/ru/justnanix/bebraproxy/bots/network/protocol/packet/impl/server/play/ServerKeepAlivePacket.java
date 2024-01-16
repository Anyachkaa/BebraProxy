package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerKeepAlivePacket extends Packet {
    private long keepaliveId;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        if (protocol >= 340) {
            out.writeLong(this.keepaliveId);
        } else {
            out.writeVarInt((int) this.keepaliveId);
        }
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        if (protocol >= 340) {
            this.keepaliveId = in.readLong();
        } else {
            this.keepaliveId = in.readVarInt();
        }
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x00, 47), new Protocol(0x1F, 107, 108, 109, 110, 210, 315, 316, 335, 338, 340));
    }
}