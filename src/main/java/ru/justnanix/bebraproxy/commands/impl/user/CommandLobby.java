package ru.justnanix.bebraproxy.commands.impl.user;

import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;

@Command.CommandInfo(
        name = "lobby",
        desc = "Телепортирует вас в лобби",
        allowedStates = {ConnectionInfo.LIMBO, ConnectionInfo.REMOTE})
public class CommandLobby extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) {
        BebraProxy.getInstance().getServer().connectPlayerToLobby(player);
    }
}