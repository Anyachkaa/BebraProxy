package ru.justnanix.bebraproxy.commands.impl.admin;

import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.player.plan.Plan;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;
import ru.justnanix.bebraproxy.utils.network.PacketUtil;

@Command.CommandInfo(
        name = "alert",
        desc = "Выводит сообщение всем игрокам",
        usage = "[fadeIn] [fadeOut] [stay] [message]",
        allowedPlans = {Plan.ADMIN})
public class CommandAlert extends Command {
    @Override
    public void onCommand(ProxiedPlayer sender, String[] args) {
        int fadeIn = Integer.parseInt(args[0]);
        int fadeOut = Integer.parseInt(args[1]);
        int stay = Integer.parseInt(args[2]);
        StringBuilder out = new StringBuilder(args[3]);
        for (int i = 4; i < args.length; ++i)
            out.append(" ").append(args[i]);

        if (out.toString().isEmpty()) {
            ChatUtil.sendChatMessage("&cТы не можешь отправить пустое сообщение!", sender, true);
            return;
        }

        BebraProxy.getInstance().getServer().getPlayers().forEach(player ->
                PacketUtil.sendTitle(player, "&7[&4ВНИМАНИЕ!&7]", "&n" + out, fadeIn, stay, fadeOut));
    }
}