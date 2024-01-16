package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.data.Difficulty;
import ru.justnanix.bebraproxy.network.data.Dimension;
import ru.justnanix.bebraproxy.network.data.Gamemode;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ServerRespawnPacket extends Packet {
    private Dimension dimension;
    private Difficulty difficulty;
    private Gamemode gamemode;
    private String level_type;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeInt(this.dimension.getId());
        out.writeByte(this.difficulty.getId());
        out.writeByte(this.gamemode.getId());
        out.writeString(this.level_type);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.dimension = Dimension.getById(in.readInt());
        this.difficulty = Difficulty.getById(in.readUnsignedByte());
        this.gamemode = Gamemode.getById(in.readUnsignedByte());
        this.level_type = in.readString(128);
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(
                new Protocol(0x07, 47),
                new Protocol(0x33, 107, 108, 109, 110, 210, 315, 316),
                new Protocol(0x34, 335),
                new Protocol(0x35, 338, 340)
        );
    }
}