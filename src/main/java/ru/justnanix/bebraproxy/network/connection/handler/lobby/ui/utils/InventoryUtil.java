package ru.justnanix.bebraproxy.network.connection.handler.lobby.ui.utils;

import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.handler.lobby.ui.Gui;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerCloseWindowPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerOpenWindowPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerWindowItemsPacket;

public class InventoryUtil {
    public static void openInventory(Gui GUI, ProxiedPlayer player) {
        if (player.getCurrentGui() != null) {
            closeInventory(player);
        }

        player.getConnectMgr().sendPacket(new ServerOpenWindowPacket(GUI.getWindowID(), GUI.getType(), GUI.getName(), GUI.getSlots()));
        player.getConnectMgr().sendPacket(new ServerWindowItemsPacket(GUI.getWindowID(), GUI.getItems()));

        GUI.onOpen();
        player.setCurrentGui(GUI);
    }

    public static void closeInventory(ProxiedPlayer player) {
        player.getConnectMgr().sendPacket(new ServerCloseWindowPacket(player.getCurrentGui().getWindowID()));
        player.setCurrentGui(null);
    }

    public static void reopenInventory(ProxiedPlayer player) {
        openInventory(player.getCurrentGui(), player);
    }
}