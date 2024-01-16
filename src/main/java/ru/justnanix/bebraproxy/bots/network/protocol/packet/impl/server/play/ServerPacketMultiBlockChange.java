package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.play;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.mc.protocol.util.NetUtil;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetInput;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

public class ServerPacketMultiBlockChange extends Packet {
    private BlockChangeRecord[] records;
    private byte[] customData;

    public ServerPacketMultiBlockChange() {
    }

    @Override
    public void read(PacketBuffer in, int protocol) {
        if (!in.bot) {
            customData = in.readByteArray();
            return;
        }

        int chunkX = in.readInt();
        int chunkZ = in.readInt();
        this.records = new BlockChangeRecord[in.readVarInt()];

        for (int index = 0; index < this.records.length; ++index) {
            try {
                short pos = in.readShort();
                BlockState block = NetUtil.readBlockState(new ByteBufNetInput(in));
                int x = (chunkX << 4) + (pos >> 12 & 15);
                int y = pos & 255;
                int z = (chunkZ << 4) + (pos >> 8 & 15);
                this.records[index] = new BlockChangeRecord(new Position(x, y, z), block);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeBytes(customData);
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x10, 340));
    }

    public BlockChangeRecord[] getRecords() {
        return records;
    }
}
