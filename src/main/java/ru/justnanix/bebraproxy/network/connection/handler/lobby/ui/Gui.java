package ru.justnanix.bebraproxy.network.connection.handler.lobby.ui;

import lombok.Data;
import lombok.Getter;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerCloseWindowPacket;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.data.ItemStack;
import ru.justnanix.bebraproxy.network.data.WindowAction;
import ru.justnanix.bebraproxy.network.data.WindowType;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerSetSlotPacket;
import ru.justnanix.bebraproxy.network.connection.handler.lobby.ui.utils.UIUtil;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class Gui {
    public final int windowID;
    public final WindowType type;
    public final String name;
    public final boolean isClosable;
    public final ProxiedPlayer player;
    public List<ItemStack> items = new ArrayList<>();
    @Getter
    private int slots;

    public Gui(ProxiedPlayer player, String name) {
        this(player, name, false);
    }

    public Gui(ProxiedPlayer player, String name, boolean isClosable) {
        this.type = WindowType.CHEST;
        this.windowID = 1;
        this.name = name;
        this.isClosable = isClosable;
        this.player = player;

        init();
    }

    public void init() {

    }

    public void onAction(WindowAction action, ItemStack itemStack, int slot, int button) {
        if (slot >= 0 && items.size() > slot && items.get(slot).getId() != 0) {
            UIUtil.reopenInventory(player, this);
        }
    }

    public void onOpen() {
    }

    public void onClose() {
        player.getConnectMgr().sendPacket(new ServerCloseWindowPacket(windowID));
    }

    public void addItem(ItemStack item) {
        this.items.add(item);
    }

    public void setItem(ItemStack item, int slot) {
        this.items.set(slot, item);
        this.player.getConnectMgr().sendPacket(new ServerSetSlotPacket(windowID, slot, item));
    }
}