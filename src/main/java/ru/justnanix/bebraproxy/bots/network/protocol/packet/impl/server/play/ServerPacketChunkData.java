package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.play;

import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.util.NetUtil;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetInput;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;

import java.util.Arrays;
import java.util.List;

public class ServerPacketChunkData extends Packet {
    private Column column;
    private byte[] customData;

    public ServerPacketChunkData() {
    }

    @Override
    public void read(PacketBuffer buf, int protocol) {
        if (!buf.bot) {
            customData = buf.readByteArray();
            return;
        }

        NetInput in = new ByteBufNetInput(buf);

        try {
            int x = in.readInt();
            int z = in.readInt();
            boolean fullChunk = in.readBoolean();
            int chunkMask = in.readVarInt();
            byte[] data = in.readBytes(in.readVarInt());
            CompoundTag[] tileEntities = new CompoundTag[in.readVarInt()];

            for (int i = 0; i < tileEntities.length; ++i) {
                tileEntities[i] = NetUtil.readNBT(in);
            }

            this.column = NetUtil.readColumn(data, x, z, fullChunk, false, chunkMask, tileEntities);

            for (int i = 0; i < column.getChunks().length; i++) {
                if (column.getChunks()[i] == null) {
                    column.getChunks()[i] = new Chunk(false);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeBytes(customData);
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x20, 340));
    }

    public Column getColumn() {
        return column;
    }
}
