package ru.justnanix.bebraproxy.network.packet.impl.server.play;

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
public class ServerUpdateScorePacket extends Packet {
    private String scoreName;
    private int action;
    private String objectiveName;
    private int value;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(this.scoreName);
        out.writeByte(this.action);
        out.writeString(this.objectiveName);
        if (action != 1) {
            out.writeVarInt(this.value);
        }
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.scoreName = in.readString(128);
        this.action = in.readByte();
        this.objectiveName = in.readString(32767);
        if (action != 1) {
            this.value = in.readVarInt();
        }
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x3C, 47), new Protocol(0x42, 107, 108, 109, 110, 210, 315, 316), new Protocol(0x44, 335), new Protocol(0x45, 338, 340));
    }
}