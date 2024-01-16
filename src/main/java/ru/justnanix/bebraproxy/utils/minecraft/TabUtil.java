package ru.justnanix.bebraproxy.utils.minecraft;

import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.commands.impl.admin.CommandStatus;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerPlayerListHeaderFooter;

import java.util.concurrent.TimeUnit;

public class TabUtil {
    private static long freeMemory;
    private static long totalMemory;
    private static long inUseMemory;
    private static double cpu;

    private static int allBots;

    static {
        BebraProxy.simpleTasks.scheduleAtFixedRate(() -> {
            freeMemory = Runtime.getRuntime().freeMemory();
            totalMemory = Runtime.getRuntime().totalMemory();
            inUseMemory = totalMemory - freeMemory;

            try {
                cpu = CommandStatus.getProcessCpuLoad();
            } catch (Throwable e) {
                e.printStackTrace();
            }

            try {
                allBots = BebraProxy.getInstance().getServer().getPlayers().stream()
                        .mapToInt(p -> p.getBotManager().getBots().size()).sum();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }, 1L, 1L, TimeUnit.SECONDS);
    }

    public static void updateTab(ProxiedPlayer player) {
        if (!player.isConnectedToRemote() || !player.getOptionsManager().getOptionByName("Таблист сервера").asBooleanOption().isEnabled()) {
            player.getConnectMgr().sendPacket(new ServerPlayerListHeaderFooter(ChatUtil.fixColor(
                    "\n &f&m-----------------------&r &l&cBebra&rProxy &m-----------------------" +
                            "\n" +
                            "\n" +
                            "&7Ник: &c" + player.getAccount().getKeyName() +
                            "\n" +
                            "&7Тариф: &c" + player.getAccount().getPlan().getPrefix() +
                            "\n" +
                            "&7Онлайн: &c" + BebraProxy.getInstance().getServer().getPlayers().stream().filter(ProxiedPlayer::isAuthorized).count() +
                            " &7| В лобби: &c" + BebraProxy.getInstance().getServer().getPlayers().stream().filter(p -> p.getConnectionInfo() == ConnectionInfo.LOBBY).count() +
                            "\n" +
                            "\n" +
                            "&7Боты: &c" + player.getBotManager().getBots().size() + "/" + player.getAccount().getPlan().getMaxBots() +
                            "&7 | Всего ботов: &c" + allBots + "/10000" +
                            "\n" +
                            "&7ЦП: &c" + cpu + "% &7ОЗУ: &c" + inUseMemory / 1024 / 1024 + "MB" +
                            "\n"
            ), ChatUtil.fixColor(
                    "\n" +
                            "&7Сессия: &c" + (!player.isConnectedToRemote() ? "В лобби" : player.getRemoteConnectMgr().getRemoteServerInfo().getHost()) +
                            "\n\n" +
                            " &f&m-----------------------&r &l&cBebra&rProxy &m-----------------------" +
                            "\n"
            )));
        }
    }
}
