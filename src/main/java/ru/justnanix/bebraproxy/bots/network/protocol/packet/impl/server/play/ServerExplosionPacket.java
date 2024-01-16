package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.play;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.data.BlockPos;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerExplosionPacket extends Packet {
    private double posX;
    private double posY;
    private double posZ;
    private float strength;
    private List<BlockPos> affectedBlockPositions;
    private float motionX;
    private float motionY;
    private float motionZ;


    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeFloat((float) this.posX);
        out.writeFloat((float) this.posY);
        out.writeFloat((float) this.posZ);
        out.writeFloat(this.strength);
        out.writeInt(this.affectedBlockPositions.size());
        int i = (int) this.posX;
        int j = (int) this.posY;
        int k = (int) this.posZ;

        for (BlockPos blockpos : this.affectedBlockPositions) {
            int l = blockpos.getX() - i;
            int i1 = blockpos.getY() - j;
            int j1 = blockpos.getZ() - k;
            out.writeByte(l);
            out.writeByte(i1);
            out.writeByte(j1);
        }

        out.writeFloat(this.motionX);
        out.writeFloat(this.motionY);
        out.writeFloat(this.motionZ);

    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.posX = (double) in.readFloat();
        this.posY = (double) in.readFloat();
        this.posZ = (double) in.readFloat();
        this.strength = in.readFloat();
        int i = in.readInt();
        this.affectedBlockPositions = Lists.<BlockPos>newArrayListWithCapacity(i);
        int j = (int) this.posX;
        int k = (int) this.posY;
        int l = (int) this.posZ;

        for (int i1 = 0; i1 < i; ++i1) {
            int j1 = in.readByte() + j;
            int k1 = in.readByte() + k;
            int l1 = in.readByte() + l;
            this.affectedBlockPositions.add(new BlockPos(j1, k1, l1));
        }

        this.motionX = in.readFloat();
        this.motionY = in.readFloat();
        this.motionZ = in.readFloat();
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x1c, 340));
    }
}
