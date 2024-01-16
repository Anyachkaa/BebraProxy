package ru.justnanix.bebraproxy.commands.impl.admin;

import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.player.plan.Plan;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

@Command.CommandInfo(
        name = "free",
        desc = "Вызывает System.gc()",
        allowedPlans = {Plan.ADMIN})
public class CommandFree extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) {
        System.gc();
        ChatUtil.sendChatMessage("System.gc() вызван", player, true);
    }
}