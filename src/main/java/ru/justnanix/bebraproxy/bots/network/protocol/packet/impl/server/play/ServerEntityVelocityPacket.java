package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public @Data class ServerEntityVelocityPacket extends Packet {
    private int entityID;
    private int motionX;
    private int motionY;
    private int motionZ;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeVarInt(this.entityID);
        out.writeShort(this.motionX);
        out.writeShort(this.motionY);
        out.writeShort(this.motionZ);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.entityID = in.readVarInt();
        this.motionX = in.readShort();
        this.motionY = in.readShort();
        this.motionZ = in.readShort();
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x3E, 340));
    }
}
