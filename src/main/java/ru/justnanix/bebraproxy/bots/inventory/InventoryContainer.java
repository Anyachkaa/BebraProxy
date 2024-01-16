package ru.justnanix.bebraproxy.bots.inventory;

import lombok.Getter;
import lombok.Setter;
import ru.justnanix.bebraproxy.bots.Bot;
import ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.client.play.ClientPlayerWindowActionPacket;
import ru.justnanix.bebraproxy.network.data.ItemStack;
import ru.justnanix.bebraproxy.network.data.WindowAction;

import java.util.List;

@Getter
@Setter
public class InventoryContainer {
    private final int windowID;
    private final List<ItemStack> items;

    private String name;
    private int transaction = 0;

    public InventoryContainer(int windowID, List<ItemStack> items, String name) {
        this.windowID = windowID;
        this.items = items;
        this.name = name;
    }

    public void slotClick(Bot bot, short slot, int button, WindowAction action) {
        bot.getSession().sendPacket(new ClientPlayerWindowActionPacket(windowID, slot, button, action, transaction++, ItemStack.AIR));
    }
}
