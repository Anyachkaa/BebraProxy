package ru.justnanix.bebraproxy.commands.impl.admin;

import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.player.plan.Plan;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

@Command.CommandInfo(
        name = "status",
        desc = "Показывает информацию о использовании ресурсов прокси",
        allowedPlans = {Plan.ADMIN})
public class CommandStatus extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();
        long inUseMemory = totalMemory - freeMemory;
        int inUse = (int) (inUseMemory * 100L / maxMemory);
        double cpu = getProcessCpuLoad();
        String bots = (String.valueOf(getAllBots()).length() >= 1500) ? "&c" + getAllBots() + "/10000" : "&a" + getAllBots() + "/10000";
        ChatUtil.sendChatMessage("", player, false);
        ChatUtil.sendChatMessage("&7&l>>&f&m-----------------------------&r&7&l<<&r", player, false);
        ChatUtil.sendChatMessage("&c>> &7Выделено памяти: &a" + humanReadableByteCount(maxMemory), player, false);
        ChatUtil.sendChatMessage("&c>> &7Используется памяти: &a" + humanReadableByteCount(inUseMemory) + " &7(&a" + inUse + "%&7)", player, false);
        ChatUtil.sendChatMessage("&c>> &7Используется ЦП: &a" + cpu + "&7%", player, false);
        ChatUtil.sendChatMessage("&c>> &7Всего ботов: " + bots, player, false);
        ChatUtil.sendChatMessage("&7&l>>&f&m-----------------------------&r&7&l<<&r", player, false);
    }

    public static int getAllBots() {
        int allBots = 0;
        for (ProxiedPlayer p : BebraProxy.getInstance().getServer().getPlayers())
            allBots += p.getBotManager().getBots().size();

        return allBots;
    }
    public static double getProcessCpuLoad() throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});
        if (list.isEmpty()) {
            return Double.NaN;
        }

        Attribute att = (Attribute) list.get(0);
        Double value = (Double) att.getValue();
        if (value != -1.0) {
            return (double) ((int) (value * 1000.0)) / 10.0;
        }

        return Double.NaN;
    }

    private String humanReadableByteCount(long bytes) {
        if (bytes < 1536L) {
            return bytes + " B";
        }

        int exp = (int) (Math.log((double) bytes) / Math.log(1536.0));
        String pre = String.valueOf("KMGTPE".charAt(exp - 1));
        return String.format("%.1f &7%sB", (double) bytes / Math.pow(1024.0, exp), pre);
    }
}