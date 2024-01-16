package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.client.play;

import com.github.steveice10.mc.protocol.data.MagicValues;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.network.data.BlockFace;
import ru.justnanix.bebraproxy.network.data.PlayerAction;
import ru.justnanix.bebraproxy.network.data.Position;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientPlayerActionPacket extends Packet {
    private PlayerAction action;
    private Position position;
    private BlockFace face;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeVarInt(MagicValues.value(Integer.class, this.action));
        writePosition(out, this.position);
        out.writeByte(MagicValues.value(Integer.class, this.face));
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.action = MagicValues.key(PlayerAction.class, in.readVarInt());
        this.position = readPosition(in);
        this.face = MagicValues.key(BlockFace.class, in.readUnsignedByte());
    }

    public PlayerAction getAction() {
        return this.action;
    }

    public Position getPosition() {
        return this.position;
    }

    public BlockFace getFace() {
        return this.face;
    }


    private Position readPosition(PacketBuffer in) {
        long val = in.readLong();
        int x = (int) (val >> 38);
        int y = (int) (val >> 26 & 4095L);
        int z = (int) (val << 38 >> 38);
        return new Position(x, y, z);
    }

    private static void writePosition(PacketBuffer out, Position pos) throws IOException {
        long x = (int) pos.getX() & 67108863;
        long y = (int) pos.getY() & 4095;
        long z = (int) pos.getZ() & 67108863;
        out.writeLong(x << 38 | y << 26 | z);
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x00, 340));
    }

}
