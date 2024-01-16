package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.network.data.ItemStack;
import ru.justnanix.bebraproxy.network.data.WindowAction;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientPlayerWindowActionPacket extends Packet {
    private int windowId;
    private short slot;
    private int button;
    private WindowAction mode;
    private int action;
    private ItemStack item;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeByte(this.windowId);
        out.writeShort(this.slot);
        out.writeByte(this.button);
        out.writeShort(this.action);
        if (protocol >= 110) {
            out.writeVarInt(this.mode.getId());
        } else {
            out.writeByte(this.mode.getId());
        }
        out.writeItemStack(this.item);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.windowId = in.readByte();
        this.slot = in.readShort();
        this.button = in.readByte();
        this.action = in.readShort();
        if (protocol >= 110) {
            this.mode = WindowAction.getById(in.readVarInt());
        } else {
            this.mode = WindowAction.getById(in.readByte());
        }
        this.item = in.readItemStack();
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x0E, 47), new Protocol(0x07, 110, 340));
    }
}