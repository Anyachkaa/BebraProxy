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
@NoArgsConstructor
@AllArgsConstructor
public class ClientCustomPayloadPacket extends Packet {
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
        this.data = in.readByteArray();
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(
                new Protocol(0x17, 47),
                new Protocol(0x09, 107, 108, 110, 315, 316, 338, 340),
                new Protocol(0x0A, 335)
        );
    }
}