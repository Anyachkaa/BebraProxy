package ru.justnanix.bebraproxy.network.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.data.ItemStack;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServerSetSlotPacket extends Packet {
    private int windowId;
    private int slot;
    private ItemStack item;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeByte(this.windowId);
        out.writeShort(this.slot);
        out.writeItemStack(this.item);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.windowId = in.readByte();
        this.slot = in.readShort();
        this.item = in.readItemStack();
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x2F, 47), new Protocol(0x16, 107, 108, 109, 110, 210, 315, 316, 335, 338, 340));
    }
}