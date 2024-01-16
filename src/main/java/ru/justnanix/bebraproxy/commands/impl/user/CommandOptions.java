package ru.justnanix.bebraproxy.commands.impl.user;

import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.network.connection.handler.lobby.ui.gui.GuiOptions;

@Command.CommandInfo(
        name = "options",
        desc = "Показывает меню настроек",
        allowedStates = {ConnectionInfo.LIMBO, ConnectionInfo.LOBBY, ConnectionInfo.REMOTE})
public class CommandOptions extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        player.setCurrentGui(new GuiOptions(player));
        player.getCurrentGui().onOpen();
    }
}
