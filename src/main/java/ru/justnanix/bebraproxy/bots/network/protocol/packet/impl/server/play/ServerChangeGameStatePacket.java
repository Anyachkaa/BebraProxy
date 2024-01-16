package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.data.Effect;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerChangeGameStatePacket extends Packet {
    private Effect effect;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeByte(effect.getEffectReason());
        out.writeFloat(effect.getValue());
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.effect = new Effect(in.readByte(), in.readFloat());
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x2B, 47), new Protocol(0x1E, 340));
    }
}