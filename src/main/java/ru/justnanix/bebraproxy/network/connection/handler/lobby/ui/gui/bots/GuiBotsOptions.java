package ru.justnanix.bebraproxy.network.connection.handler.lobby.ui.gui.bots;

import net.md_5.bungee.api.ChatColor;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.handler.lobby.ui.Gui;
import ru.justnanix.bebraproxy.network.connection.handler.lobby.ui.utils.UIUtil;
import ru.justnanix.bebraproxy.player.options.Option;
import ru.justnanix.bebraproxy.player.options.impl.BooleanOption;
import ru.justnanix.bebraproxy.player.options.impl.SwitchOption;
import ru.justnanix.bebraproxy.player.options.impl.ValueOption;
import ru.justnanix.bebraproxy.network.data.ItemStack;
import ru.justnanix.bebraproxy.network.data.WindowAction;
import ru.justnanix.bebraproxy.network.data.WindowType;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerOpenWindowPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerSetSlotPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerWindowItemsPacket;

import java.util.List;
import java.util.stream.Collectors;

public class GuiBotsOptions extends Gui {
    public GuiBotsOptions(ProxiedPlayer player) {
        super(player, "");
        this.setSlots(9 * 3);
        this.onOpen();
    }

    @Override
    public void onOpen() {
        items.clear();
        player.getOptionsManager().elements.forEach(options -> {
            if (options.getCategory() == Option.Category.BOTS) {
                addItem(UIUtil.option(options));
            }
        });

        player.getConnectMgr().sendPacket(new ServerOpenWindowPacket(windowID, WindowType.CHEST, "{\"text\":\"§8Настройки §cботов\"}", 9 * 3));
        player.getConnectMgr().sendPacket(new ServerWindowItemsPacket(windowID, items));
    }

    @Override
    public void onAction(WindowAction action, ItemStack itemStack, int slot, int button) {
        List<Option> bots = player.getOptionsManager().elements.stream().filter(el -> el.getCategory().equals(Option.Category.BOTS)).collect(Collectors.toList());

        for (int i = 0; i < bots.size(); i++) {
            Option option = bots.get(i);

            if (itemStack != null && action == WindowAction.CLICK_ITEM && option.getName().equalsIgnoreCase(ChatColor.stripColor(itemStack.getName()))) {
                if (option instanceof BooleanOption) {
                    option.asBooleanOption().toggle();
                } else if (option instanceof SwitchOption) {
                    option.asSwitchOption().switchOpt();
                } else if (option instanceof ValueOption) {
                    option.asValueOption().set();
                    return;
                }

                player.getConnectMgr().sendPacket(new ServerSetSlotPacket(-1, -1, ItemStack.AIR));
                this.setItem(UIUtil.option(option), i);

                return;
            }
        }

        super.onAction(action, itemStack, slot, button);
    }
}
