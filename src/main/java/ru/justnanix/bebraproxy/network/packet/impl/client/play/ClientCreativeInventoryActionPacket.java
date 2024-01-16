package ru.justnanix.bebraproxy.network.packet.impl.client.play;

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
public class ClientCreativeInventoryActionPacket extends Packet {

    private int slot;
    private ItemStack clicked;

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getClickedItem() {
        return this.clicked;
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeShort(this.slot);
        out.writeItemStack(this.clicked);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.slot = in.readShort();
        this.clicked = in.readItemStack();
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x1B, 340));
    }
}
