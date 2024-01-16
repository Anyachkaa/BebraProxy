package ru.justnanix.bebraproxy.network.packet.impl.client.play;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.data.Position;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
public class ClientTabCompletePacket extends Packet {
    private String text;
    private Position position;
    private boolean assumeCMD;

    public ClientTabCompletePacket(final String text) {
        this(text, null);
    }

    public ClientTabCompletePacket(final String text, final Position position) {
        this.text = text;
        this.position = position;
    }

    public ClientTabCompletePacket(final String text, final boolean assumeCMD, final Position position) {
        this.text = text;
        this.assumeCMD = assumeCMD;
        this.position = position;
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(this.text);
        if (protocol >= 109) {
            out.writeBoolean(this.assumeCMD);
        }
        out.writeBoolean(this.position != null);
        if (this.position != null) {
            out.writePosition(position);
        }
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.text = in.readString();
        if (protocol >= 109) {
            this.assumeCMD = in.readBoolean();
        }
        this.position = (in.readBoolean() ? in.readPosition() : null);
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x14, 47), new Protocol(0x01, 109, 110, 340));
    }
}