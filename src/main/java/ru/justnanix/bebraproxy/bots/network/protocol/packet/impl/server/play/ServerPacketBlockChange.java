package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.play;

import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;
import com.github.steveice10.mc.protocol.util.NetUtil;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetInput;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

public class ServerPacketBlockChange extends Packet {
    private BlockChangeRecord record;
    private byte[] customData;

    public ServerPacketBlockChange() {
    }

    @Override
    public void read(PacketBuffer in, int protocol) {
        if (!in.bot) {
            customData = in.readByteArray();
            return;
        }

        NetInput input = new ByteBufNetInput(in);

        try {
            this.record = new BlockChangeRecord(NetUtil.readPosition(input), NetUtil.readBlockState(input));
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
        return Arrays.asList(new Protocol(0x0B, 340));
    }

    public BlockChangeRecord getRecord() {
        return this.record;
    }
}
