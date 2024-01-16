package ru.justnanix.bebraproxy.network.connection.handler.lobby.ui.gui;

import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.handler.lobby.ui.Gui;
import ru.justnanix.bebraproxy.network.connection.handler.lobby.ui.gui.bots.GuiBotsOptions;
import ru.justnanix.bebraproxy.network.data.ItemStack;
import ru.justnanix.bebraproxy.network.data.WindowAction;
import ru.justnanix.bebraproxy.network.data.WindowType;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerOpenWindowPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerWindowItemsPacket;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

public class GuiOptions extends Gui {
    public GuiOptions(ProxiedPlayer player) {
        super(player, "");
        this.setSlots(9 * 3);
    }

    @Override
    public void onOpen() {
        items.clear();

        for (int i = 0; i < this.getSlots(); i++) {
            this.addItem(new ItemStack("", 160, 1, 15));
        }

        this.setItem(new ItemStack(404, ChatUtil.fixColor("&7Настройки &cBebra&fProxy")), 11);
        this.setItem(new ItemStack(91, ChatUtil.fixColor("&7Настройки &cботов")), 15);

        player.getConnectMgr().sendPacket(new ServerOpenWindowPacket(windowID, WindowType.CHEST, "{\"text\":\"§8Настройки\"}", this.getSlots()));
        player.getConnectMgr().sendPacket(new ServerWindowItemsPacket(windowID, items));
    }

    @Override
    public void onAction(WindowAction action, ItemStack itemStack, int slot, int button) {
        if (action == WindowAction.CLICK_ITEM) {
            if (slot == 11) {
                player.setCurrentGui(new GuiProxyOptions(player));
                return;
            } else if (slot == 15) {
                player.setCurrentGui(new GuiBotsOptions(player));
                return;
            }
        }

        super.onAction(action, itemStack, slot, button);
    }
}
