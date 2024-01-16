package ru.justnanix.bebraproxy.network.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.data.WindowType;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServerOpenWindowPacket extends Packet {
    private int windowId;
    private WindowType type;
    private String name;
    private int slots;
    private int ownerEntityId;

    public ServerOpenWindowPacket(int windowId, WindowType type, String name, int slots) {
        this(windowId, type, name, slots, 0);
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeByte(this.windowId);
        out.writeString(this.type.getValue());
        out.writeString(name);
        out.writeByte(this.slots);

        if (this.type == WindowType.HORSE) {
            out.writeInt(this.ownerEntityId);
        }
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.windowId = in.readUnsignedByte();
        this.type = WindowType.getById(in.readString());
        this.name = in.readString();
        this.slots = in.readUnsignedByte();

        if (this.type == WindowType.HORSE) {
            this.ownerEntityId = in.readInt();
        }
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Collections.singletonList(new Protocol(19, 340));
    }
}