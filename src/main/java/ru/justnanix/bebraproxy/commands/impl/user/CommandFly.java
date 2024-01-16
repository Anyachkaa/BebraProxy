package ru.justnanix.bebraproxy.commands.impl.user;

import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;
import ru.justnanix.bebraproxy.utils.network.PacketUtil;

@Command.CommandInfo(
        name = "fly",
        desc = "Управление флаем у игрока",
        usage = "[true/false]",
        allowedStates = ConnectionInfo.REMOTE)
public class CommandFly extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        boolean state = Boolean.parseBoolean(args[0]);
        PacketUtil.fly(player, state);
        ChatUtil.sendChatMessage("&7Полёт успешно " + (state ? "&aвключён" : "&cвыключен") + "&7!", player, true);
    }
}
