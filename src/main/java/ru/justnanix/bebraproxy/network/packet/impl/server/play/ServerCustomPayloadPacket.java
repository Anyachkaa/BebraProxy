package ru.justnanix.bebraproxy.network.packet.impl.server.play;

import io.netty.buffer.ByteBufUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ServerCustomPayloadPacket extends Packet {
    private String channel;
    private byte[] data;

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
            this.data = ByteBufUtil.getBytes(in.readBytes(i));
        } else {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x18, 340));
    }
}
