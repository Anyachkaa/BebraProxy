package ru.justnanix.bebraproxy.network.packet.impl.server.play;

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

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServerJoinGamePacket extends Packet {
    private int entityId;
    private Gamemode gamemode;
    private Dimension dimension;
    private Difficulty difficulty;
    private int maxPlayers;
    private String levelType;
    private boolean reduced_debug;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeInt(this.entityId);
        out.writeByte(this.gamemode.getId());

        if (protocol >= 108) {
            out.writeInt(this.dimension.getId());
        } else {
            out.writeByte(this.dimension.getId());
        }

        out.writeByte(this.difficulty.getId());
        out.writeByte(this.maxPlayers);
        out.writeString(this.levelType);
        out.writeBoolean(this.reduced_debug);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.entityId = in.readInt();
        this.gamemode = Gamemode.getById(in.readUnsignedByte());
        if (protocol >= 108) {
            this.dimension = Dimension.getById(in.readInt());
        } else {
            this.dimension = Dimension.getById(in.readByte());
        }
        this.difficulty = Difficulty.getById(in.readUnsignedByte());
        this.maxPlayers = in.readUnsignedByte();
        this.levelType = in.readString(128);

        if (this.levelType == null) {
            this.levelType = "default";
        }

        this.reduced_debug = in.readBoolean();
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(
                new Protocol(0x01, 47),
                new Protocol(0x23, 107, 108, 109, 110, 210, 315, 316, 335, 338, 340)
        );
    }
}