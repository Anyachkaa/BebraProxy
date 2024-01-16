package ru.justnanix.bebraproxy.commands.impl.bot;

import ru.justnanix.bebraproxy.bots.Bot;
import ru.justnanix.bebraproxy.bots.BotConnection;
import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.network.connection.ServerInfo;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;
import ru.justnanix.bebraproxy.utils.proxy.ThreadUtils;

import java.util.Random;

@Command.CommandInfo(
        name = "connect",
        desc = "Подключение ботов",
        usage = "[кол-во] [задержка] | stop",
        allowedStates = ConnectionInfo.REMOTE)
public class CommandConnect extends Command {
    private boolean connecting = false;

    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        if (args[0].equalsIgnoreCase("stop")) {
            connecting = false;
            ChatUtil.sendChatMessage("&7Подключение ботов остановлено.", player, true);
        } else {
            if (connecting) {
                ChatUtil.sendChatMessage("&7Вы уже подключаете ботов! Остановить подключение - connect stop", player, true);
                return;
            }

            int count = Integer.parseInt(args[0]);
            int delay = Integer.parseInt(args[1]);

            if (player.getBotManager().getBots().size() >= player.getAccount().getPlan().getMaxBots()) {
                ChatUtil.sendChatMessage("&cВы достигли лимита ботов.", player, true);
                return;
            }

            if (count > player.getAccount().getPlan().getMaxBots()) {
                ChatUtil.sendChatMessage("&cЭто превышает ваш максимальный лимит ботов!", player, true);
                return;
            }

            if (delay < 500) {
                ChatUtil.sendChatMessage("&cМинимальная задержка - 500мс", player, true);
                return;
            }

            if (delay > 10000) {
                ChatUtil.sendChatMessage("&cМаксимальная задержка - 10000мс", player, true);
                return;
            }

            if (count <= 0) {
                ChatUtil.sendChatMessage("&cНекорректно введено кол-во ботов!", player, true);
                return;
            }

            connecting = true;
            Random random = new Random(System.currentTimeMillis());

            for (int i = 0; i < count && player.isConnectedToRemote() && connecting; i++) {
                if (player.getBotManager().getBots().size() >= player.getAccount().getPlan().getMaxBots()) {
                    ChatUtil.sendChatMessage("&cВы достигли лимита ботов.", player, true);
                    break;
                }

                try {
                    String nick = "";

                    switch (player.getOptionsManager().getOptionByName("Ник ботов").asSwitchOption().getCurrentVal()) {
                        case "Префикс (BEBRAPROXY_num)":
                            nick = "BEBRAPROXY_" + random.nextInt(9999);
                            break;
                        case "Рандом":
                            nick = random.nextInt(999999999) + "";
                            break;
                    }

                    ChatUtil.sendChatMessage("&7[&cBOT&7] [&c" + nick + "&7] Подключение...", player, true);

                    ServerInfo info = player.getRemoteConnectMgr().getRemoteServerInfo();
                    new BotConnection(new Bot(player, nick)).connect(info.getHost(), info.getPort(), player.getProxyManager().getProxy());
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                ThreadUtils.sleep(delay);
            }

            connecting = false;
        }
    }
}
