package ru.justnanix.bebraproxy.commands.impl.bot.chat;

import ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.client.play.ClientChatPacket;
import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

@Command.CommandInfo(
        name = "chat",
        desc = "Отправляет сообщение в чат от всех ботов",
        usage = "[сообщение]")
public class CommandChat extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        StringBuilder msg = new StringBuilder();
        for (String arg : args)
            msg.append(arg).append(" ");

        player.getBotManager().getBots().forEach(bot -> bot.getSession().sendPacket(new ClientChatPacket(msg.toString())));
        ChatUtil.sendChatMessage("Боты отправили сообщение.", player, true);
    }
}
