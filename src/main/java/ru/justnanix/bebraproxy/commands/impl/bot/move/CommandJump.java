package ru.justnanix.bebraproxy.commands.impl.bot.move;

import ru.justnanix.bebraproxy.bots.Bot;
import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

@Command.CommandInfo(
        name = "jump",
        desc = "Боты прыгнут 1 раз")
public class CommandJump extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        player.getBotManager().getBots().forEach(Bot::jump);
        ChatUtil.sendChatMessage("Боты прыгнули", player, true);
    }
}
