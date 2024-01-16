package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class ServerCustomPayloadPacket extends Packet {

    private String channel;
    private PacketBuffer data;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(this.channel);
        out.writeBytes(this.data);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.channel = in.readString(20);
        int i = in.readableBytes();

        if (i >= 0 && i <= 1048576) {
            this.data = new PacketBuffer(in.readBytes(i));
        } else {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x18, 340));
    }
}
