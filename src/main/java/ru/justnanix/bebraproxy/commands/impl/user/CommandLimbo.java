package ru.justnanix.bebraproxy.commands.impl.user;

import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.network.connection.ConnectionManagerRemote;
import ru.justnanix.bebraproxy.utils.minecraft.WorldUtil;

@Command.CommandInfo(
        name = "limbo",
        desc = "Телепортирует вас в лимбо",
        allowedStates = {ConnectionInfo.LOBBY, ConnectionInfo.REMOTE})
public class CommandLimbo extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        WorldUtil.limboWorld(player);

        ConnectionManagerRemote prev = player.getRemoteConnectMgr();
        player.setRemoteConnectMgr(null);
        if (prev != null && prev.isChannelOpen()) {
            prev.getChannel().close();
        }
    }
}
