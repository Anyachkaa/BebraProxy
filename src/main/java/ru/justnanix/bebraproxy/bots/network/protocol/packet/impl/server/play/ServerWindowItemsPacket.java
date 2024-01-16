package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.data.ItemStack;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServerWindowItemsPacket extends Packet {
    private int windowId;
    private ItemStack[] itemStacks;

    public ServerWindowItemsPacket(int windowIdIn, List<ItemStack> p_i45186_2_) {
        this.windowId = windowIdIn;
        this.itemStacks = new ItemStack[p_i45186_2_.size()];

        for (int i = 0; i < this.itemStacks.length; ++i) {
            ItemStack itemstack = p_i45186_2_.get(i);
            this.itemStacks[i] = itemstack;
        }
    }

    public ServerWindowItemsPacket(int windowIdIn, ItemStack p_i45186_2_) {
        this.windowId = windowIdIn;

        this.itemStacks = new ItemStack[1];
        this.itemStacks[0] = p_i45186_2_;
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeByte(this.windowId);
        out.writeShort(this.itemStacks.length);

        for (ItemStack itemstack : this.itemStacks) {
            out.writeItemStack(itemstack);
        }
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.windowId = in.readUnsignedByte();
        int i = in.readShort();
        this.itemStacks = new ItemStack[i];

        for (int j = 0; j < i; ++j) {
            this.itemStacks[j] = in.readItemStack();
        }
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x30, 47), new Protocol(0x14, 107, 108, 315, 316, 335, 338, 340));
    }
}