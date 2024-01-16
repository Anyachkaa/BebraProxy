package ru.justnanix.bebraproxy.network.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ClientPlayerPositionRotationPacket extends Packet {
    private double x, y, z;
    private float yaw, pitch;
    private boolean ground;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeDouble(this.x);
        out.writeDouble(this.y);
        out.writeDouble(this.z);
        out.writeFloat(this.yaw);
        out.writeFloat(this.pitch);
        out.writeBoolean(this.ground);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.x = in.readDouble();
        this.y = in.readDouble();
        this.z = in.readDouble();
        this.yaw = in.readFloat();
        this.pitch = in.readFloat();
        this.ground = in.readBoolean();
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x0E, 340));
    }
}
