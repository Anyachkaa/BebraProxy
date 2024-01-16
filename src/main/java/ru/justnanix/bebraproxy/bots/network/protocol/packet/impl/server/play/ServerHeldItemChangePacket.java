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
public class ServerHeldItemChangePacket extends Packet {
    private int slot;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeByte(this.slot);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.slot = in.readByte();
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x09, 47), new Protocol(0x3A, 340));
    }
}