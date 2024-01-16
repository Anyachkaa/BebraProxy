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
@AllArgsConstructor
@NoArgsConstructor
public class ServerTabCompletePacket extends Packet {
    private String[] matches;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeVarInt(this.matches.length);
        for (final String match : this.matches) {
            out.writeString(match);
        }
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.matches = new String[in.readVarInt()];
        for (int index = 0; index < this.matches.length; ++index) {
            this.matches[index] = in.readString();
        }
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x3A, 47), new Protocol(0x0E, 107, 108, 109, 110, 315, 316, 335, 338, 340));
    }
}