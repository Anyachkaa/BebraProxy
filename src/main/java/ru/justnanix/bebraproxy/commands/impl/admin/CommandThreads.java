package ru.justnanix.bebraproxy.commands.impl.admin;

import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.player.plan.Plan;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

@Command.CommandInfo(
        name = "threads",
        desc = "Показывает информацию о запущенных потоках в прокси",
        allowedPlans = {Plan.ADMIN})
public class CommandThreads extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) {
        ChatUtil.sendChatMessage("&7&l>>&f&m-----------------------------&r&7&l<<&r", player, false);
        ChatUtil.sendChatMessage("&c>> Всего потоков: " + Thread.activeCount(), player, false);
        ChatUtil.sendChatMessage("&c>> Текущий поток: " + Thread.currentThread().getName(), player, false);
        ChatUtil.sendChatMessage("&7&l>>&f&m-----------------------------&r&7&l<<&r", player, false);
    }
}