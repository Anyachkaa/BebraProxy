package ru.justnanix.bebraproxy.commands.impl.bot.inventory;

import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.client.play.ClientHeldItemChangePacket;
import ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.client.play.ClientPlayerTryUseItemPacket;
import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

import java.util.concurrent.TimeUnit;

@Command.CommandInfo(
        name = "hotbarclick",
        desc = "Боты кликают на заданный слот в хотбаре",
        usage = "[номер слота]")
public class CommandHotbarClick extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        player.getBotManager().getBots().forEach(bot -> bot.getSession().sendPacket(new ClientHeldItemChangePacket(Integer.parseInt(args[0]))));
        BebraProxy.simpleTasks.schedule(() -> player.getBotManager().getBots().forEach(bot ->
                bot.getSession().sendPacket(new ClientPlayerTryUseItemPacket())), 250L, TimeUnit.MILLISECONDS);

        ChatUtil.sendChatMessage("Боты успешно кликнули на слот &b" + args[0], player, true);
    }
}
