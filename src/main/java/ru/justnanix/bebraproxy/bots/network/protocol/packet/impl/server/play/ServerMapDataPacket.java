package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.play;

import com.github.steveice10.mc.protocol.data.game.world.map.MapData;
import com.github.steveice10.mc.protocol.data.game.world.map.MapIcon;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetInput;
import lombok.Getter;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

@Getter
public class ServerMapDataPacket extends Packet {
    private int mapId;
    private byte scale;
    private boolean trackingPosition;
    private MapIcon[] icons;
    private MapData data;

    private byte[] customData;

    @Override
    public void read(PacketBuffer buf, int protocol) throws Exception {
        if (!buf.bot) {
            customData = buf.readByteArray();
            return;
        }

        NetInput in = new ByteBufNetInput(buf);

        this.mapId = in.readVarInt();
        this.scale = in.readByte();
        this.trackingPosition = in.readBoolean();
        this.icons = new MapIcon[in.readVarInt()];

        int columns;
        int rows;
        int x;
        int y;
        for(columns = 0; columns < this.icons.length; ++columns) {
            in.readUnsignedByte();
            in.readUnsignedByte();
            in.readUnsignedByte();
        }

        columns = in.readUnsignedByte();
        if (columns > 0) {
            rows = in.readUnsignedByte();
            x = in.readUnsignedByte();
            y = in.readUnsignedByte();
            byte[] data = in.readBytes(in.readVarInt());
            this.data = new MapData(columns, rows, x, y, data);
        }
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeBytes(customData);
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x24, 340));
    }
}
