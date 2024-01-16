package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.play;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Getter
public class ServerDestroyEntitiesPacket extends Packet {
    private int[] entityIDs;

    public ServerDestroyEntitiesPacket(int... entityIDsIn) {
        this.entityIDs = entityIDsIn;
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.entityIDs = new int[in.readVarInt()];

        for (int i = 0; i < this.entityIDs.length; ++i) {
            this.entityIDs[i] = in.readVarInt();
        }
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeVarInt(this.entityIDs.length);

        for (int entityID : this.entityIDs) {
            out.writeVarInt(entityID);
        }
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x13, 47),
                new Protocol(0x32, 340));
    }
}
