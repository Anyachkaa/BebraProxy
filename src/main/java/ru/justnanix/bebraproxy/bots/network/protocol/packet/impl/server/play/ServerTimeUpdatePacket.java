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
public class ServerTimeUpdatePacket extends Packet {
    private long worldAge;
    private long dayTime;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeLong(this.worldAge);
        out.writeLong(this.dayTime);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.worldAge = in.readLong();
        this.dayTime = in.readLong();
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x03, 47), new Protocol(0x44, 107, 108, 109, 110, 315, 316), new Protocol(0x46, 335), new Protocol(0x47, 338, 340));
    }
}