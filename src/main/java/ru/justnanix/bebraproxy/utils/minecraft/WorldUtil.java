package ru.justnanix.bebraproxy.utils.minecraft;

import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.data.Difficulty;
import ru.justnanix.bebraproxy.network.data.Dimension;
import ru.justnanix.bebraproxy.network.data.Gamemode;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.*;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.utils.network.PacketUtil;

public class WorldUtil {
    public static void dimSwitch(ProxiedPlayer player, ServerJoinGamePacket packet) {
        if (player.getDimension() == Dimension.OVERWORLD) {
            player.getConnectMgr().sendPacket(new ServerRespawnPacket(Dimension.END,
                    packet.getDifficulty(), packet.getGamemode(), packet.getLevelType()));
        } else if (player.getDimension() == Dimension.END || packet.getDimension() == Dimension.NETHER) {
            player.getConnectMgr().sendPacket(new ServerRespawnPacket(Dimension.OVERWORLD,
                    packet.getDifficulty(), packet.getGamemode(), packet.getLevelType()));
        }

        player.getConnectMgr().sendPacket(packet);
        player.getConnectMgr().sendPacket(new ServerRespawnPacket(packet.getDimension(),
                packet.getDifficulty(), packet.getGamemode(), packet.getLevelType()));

        player.setDimension(packet.getDimension());
    }

    public static void limboWorld(ProxiedPlayer player) {
        ChatUtil.sendChatMessage("&7Перемещаем вас в лимбо...", player, true);
        player.setConnectionInfo(ConnectionInfo.CONNECTING);
        ServerJoinGamePacket packet = new ServerJoinGamePacket(0, Gamemode.ADVENTURE, Dimension.END,
                Difficulty.PEACEFULL, 1, "default_1_1", false);
        dimSwitch(player, packet);

        player.getConnectMgr().sendPacket(new ServerPlayerAbilitiesPacket(false, true, true, false,
                0f, 0f));
        player.getConnectMgr().sendPacket(new ServerPlayerPosLookPacket(0, 70, 0, 180, 90));
        player.setConnectionInfo(ConnectionInfo.LIMBO);
        PacketUtil.onChangeServer(player);
        SkinUtil.showSkin(player);
    }
}
