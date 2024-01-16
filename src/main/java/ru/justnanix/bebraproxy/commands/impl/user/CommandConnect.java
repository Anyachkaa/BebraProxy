package ru.justnanix.bebraproxy.commands.impl.user;

import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;
import ru.justnanix.bebraproxy.utils.proxy.SRVResolver;

@Command.CommandInfo(
        name = "connect",
        desc = "Подключение к серверу",
        usage = "[айпи] [ник]",
        allowedStates = {ConnectionInfo.LIMBO, ConnectionInfo.LOBBY, ConnectionInfo.REMOTE})
public class CommandConnect extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        String ip = args[0];
        int port;
        String nick = player.getAccount().getKeyName();

        if (ip.contains(":")) {
            String[] sp = ip.split(":");
            ip = sp[0];
            port = Integer.parseInt(sp[1]);
        } else {
            String[] resolved = SRVResolver.getServerAddress(ip);
            ip = resolved[0];
            port = Integer.parseInt(resolved[1]);
        }

        if (ip.toLowerCase().contains("localhost") || ip.toLowerCase().contains("0.0") || ip.toLowerCase().startsWith("10.")
                || ip.toLowerCase().startsWith("127.") || ip.toLowerCase().startsWith("192.") || ip.toLowerCase().startsWith("169.")
                || ip.toLowerCase().startsWith("172.") || !ip.matches("^[A-z0-9.\\-:]*$")) {
            ChatUtil.sendChatMessage("&cНеправильный адрес!", player, true);
            return;
        }

        if (args.length == 2) {
            nick = args[1];
        }

        BebraProxy.getInstance().getServer().connectPlayerToServer(player, ip, port);
    }
}