package ru.justnanix.bebraproxy.utils.minecraft;

import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.data.scoreboard.ObjectiveMode;
import ru.justnanix.bebraproxy.network.data.scoreboard.ObjectiveType;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerDisplayScoreboardPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerScoreboardObjectivePacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerUpdateScorePacket;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class ScoreboardUtil {
    private static final DecimalFormat format = new DecimalFormat("##.##");

    public static void sendScoreboard(ProxiedPlayer player) {
        String sidebarName = format.format(System.currentTimeMillis());
        player.getConnectMgr().sendPacket(new ServerScoreboardObjectivePacket(sidebarName, ObjectiveMode.CREATE, "",
                ObjectiveType.INTEGER));

        int val = 0;
        player.getConnectMgr().sendPacket(new ServerUpdateScorePacket(ChatUtil.fixColor("┌&m-------- &cBebra&rProxy&f&m --------"),
                0, sidebarName, val--));
        player.getConnectMgr().sendPacket(new ServerUpdateScorePacket(ChatUtil.fixColor("| &7Ник: &c" +
                player.getAccount().getKeyName()), 0, sidebarName, val--));
        player.getConnectMgr().sendPacket(new ServerUpdateScorePacket(ChatUtil.fixColor("| &7Тариф: " +
                player.getAccount().getPlan().getPrefix()), 0, sidebarName, val--));
        player.getConnectMgr().sendPacket(new ServerUpdateScorePacket(ChatUtil.fixColor("| &7Окончание: &c" +
                new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss").format(player.getAccount().getExpires())), 0, sidebarName, val--));

        if (player.isConnectedToRemote()) {
            player.getConnectMgr().sendPacket(new ServerUpdateScorePacket("|", 0, sidebarName, val--));
            if (!player.getGameProfile().getName().equals(player.getRemoteConnectMgr().getRemoteGameProfile().getName())) {
                player.getConnectMgr().sendPacket(new ServerUpdateScorePacket(ChatUtil.fixColor("| &7Ник (сервер): &c"
                        + player.getRemoteConnectMgr().getRemoteGameProfile().getName()), 0, sidebarName, val--));
            }

            player.getConnectMgr().sendPacket(new ServerUpdateScorePacket(ChatUtil.fixColor("| &7Сервер: &c"
                    + player.getRemoteConnectMgr().getRemoteServerInfo().getHost()), 0, sidebarName, val--));
        }

        player.getConnectMgr().sendPacket(new ServerUpdateScorePacket(ChatUtil.fixColor("└&m----------------------------&r"),
                0, sidebarName, val));

        player.getConnectMgr().sendPacket(new ServerDisplayScoreboardPacket(1, sidebarName));
    }

    public static void sendEmptyScoreboard(ProxiedPlayer player) {
        player.getConnectMgr().sendPacket(new ServerScoreboardObjectivePacket("emptySidebar", ObjectiveMode.CREATE,
                "", ObjectiveType.INTEGER));
        player.getConnectMgr().sendPacket(new ServerDisplayScoreboardPacket(1, "emptySidebar"));
    }

    public static void updateScoreboard(ProxiedPlayer player) {
        if (!player.isConnectedToRemote() || player.getOptionsManager().getOptionByName("Scoreboard").asBooleanOption().isEnabled()) {
            sendEmptyScoreboard(player);
            sendScoreboard(player);
        }
    }
}