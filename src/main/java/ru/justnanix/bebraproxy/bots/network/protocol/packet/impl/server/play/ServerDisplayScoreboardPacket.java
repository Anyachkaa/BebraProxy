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

public class ServerDisplayScoreboardPacket extends Packet {
    private int position;
    private String scoreName;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeByte(this.position);
        out.writeString(this.scoreName);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.position = in.readByte();
        this.scoreName = in.readString(32767);
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(
                new Protocol(0x3D, 47),
                new Protocol(0x3A, 335),
                new Protocol(0x38, 107, 108, 109, 110, 210, 315, 316),
                new Protocol(0x3B, 338, 340)
        );
    }
}