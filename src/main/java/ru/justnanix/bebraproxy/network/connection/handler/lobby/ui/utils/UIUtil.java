package ru.justnanix.bebraproxy.network.connection.handler.lobby.ui.utils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.handler.lobby.ui.Gui;
import ru.justnanix.bebraproxy.player.options.Option;
import ru.justnanix.bebraproxy.player.options.impl.BooleanOption;
import ru.justnanix.bebraproxy.player.options.impl.SwitchOption;
import ru.justnanix.bebraproxy.player.options.impl.ValueOption;
import ru.justnanix.bebraproxy.network.data.ItemStack;
import ru.justnanix.bebraproxy.network.data.Skin;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerSetSlotPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerWindowItemsPacket;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

import java.util.ArrayList;
import java.util.List;

public class UIUtil {
    public static ItemStack option(Option option) {
        List<String> lores = new ArrayList<>();

        if (option instanceof BooleanOption) {
            lores.add(ChatUtil.fixColor("&7" + option.getDescription()));
            lores.add("");
            lores.add(ChatUtil.fixColor("&7Нажмите чтобы " + (((BooleanOption) option).isEnabled() ? "&cотключить" : "&aвключить")));
        } else if (option instanceof SwitchOption) {
            lores.add(ChatUtil.fixColor("&7Текущий режим - &c" + ((SwitchOption) option).getCurrentVal()));
            lores.add("");
            lores.add(ChatUtil.fixColor("&7Нажмите чтобы &cпереключить."));
        } else if (option instanceof ValueOption) {
            lores.add(ChatUtil.fixColor("&7Текущее значение - &c" + ((ValueOption) option).getValue()));
            lores.add("");
            lores.add(ChatUtil.fixColor("&7Нажмите чтобы &cизменить."));
        }

        if (option instanceof BooleanOption) {
            return new ItemStack(160, 1, ((BooleanOption) option).isEnabled() ? 5 : 14)
                    .setName("§r" + (((BooleanOption) option).isEnabled() ? "§a" : "§c") + option.getName())
                    .setLoreName(lores);
        } else if (option instanceof SwitchOption) {
            return new ItemStack(133, 1)
                    .setName("§r§a" + option.getName())
                    .setLoreName(lores);
        } else {
            return new ItemStack(386, 1)
                    .setName("§r§a" + option.getName())
                    .setLoreName(lores);
        }
    }

    public static ItemStack skull(Skin skin) {
        if (skin == null) {
            return new ItemStack(397, 1, 3);
        }

        NBTTagCompound nbt = new NBTTagCompound();

        NBTTagCompound property = new NBTTagCompound();
        property.setString("Signature", skin.getSignature());
        property.setString("Value", skin.getValue());

        NBTTagList textures = new NBTTagList();
        textures.appendTag(property);

        NBTTagCompound properties = new NBTTagCompound();
        properties.setTag("textures", textures);

        NBTTagCompound skullOwner = new NBTTagCompound();
        skullOwner.setString("Id", skin.getOwnerGameProfile().getIdAsString());
        skullOwner.setString("Name", skin.getOwnerGameProfile().getName());
        skullOwner.setTag("Properties", properties);

        nbt.setTag("SkullOwner", skullOwner);

        return new ItemStack(397, 1, 3, nbt);
    }

    public static ItemStack optionsMenu() {
        return new ItemStack(404, ChatUtil.fixColor("&7Настройки"));
    }

    public static ItemStack profileMenu(ProxiedPlayer player) {
        return skull(player.getGameProfile().getSkin()).setName(ChatUtil.fixColor("&aПрофиль"));
    }

    public static void reopenInventory(ProxiedPlayer player, Gui GUI) {
        player.getConnectMgr().sendPacket(new ServerSetSlotPacket(-1, -1, ItemStack.AIR));
        player.getConnectMgr().sendPacket(new ServerWindowItemsPacket(GUI.windowID, GUI.items));
    }

    public static void clearInventory(ProxiedPlayer player) {
        List<ItemStack> items = new ArrayList<>(44);

        for (int i = 0; i < 44; i++) {
            items.add(ItemStack.AIR);
        }

        player.getConnectMgr().sendPacket(new ServerWindowItemsPacket(0, items));
    }

    public static void loadStartItems(ProxiedPlayer player) {
        player.getConnectMgr().sendPacket(new ServerSetSlotPacket(0, 36, profileMenu(player)));
        player.getConnectMgr().sendPacket(new ServerSetSlotPacket(0, 44, optionsMenu()));
    }
}