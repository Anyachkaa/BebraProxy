package ru.justnanix.bebraproxy.commands.impl.bot.inventory;

import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.data.WindowAction;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

@Command.CommandInfo(
        name = "invclick",
        desc = "Боты кликают на заданный слот в инвентаре",
        usage = "[номер слота]")
public class CommandInvClick extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        player.getBotManager().getBots().forEach(bot -> {
            if (bot.getOpenContainer() != null) {
                bot.getOpenContainer().slotClick(bot, Short.parseShort(args[0]), 0, WindowAction.CLICK_ITEM);
            }
        });

        ChatUtil.sendChatMessage("Боты кликнули на слот &b" + args[0], player, true);
    }
}
