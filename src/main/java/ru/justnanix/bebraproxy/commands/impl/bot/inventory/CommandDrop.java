package ru.justnanix.bebraproxy.commands.impl.bot.inventory;

import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.data.WindowAction;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Command.CommandInfo(
        name = "drop",
        desc = "Боты выкидывают все предметы из инвентарей")
public class CommandDrop extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        AtomicInteger count = new AtomicInteger();
        player.getBotManager().getBots().forEach(bot -> {
            if (bot.getInventory() != null && !bot.getInventory().getItems().isEmpty()) {
                BebraProxy.simpleTasks.schedule(() -> {
                    for (short i = 0; i < bot.getInventory().getItems().size(); i++) {
                        if (bot.getInventory().getItems().get(i) != null && bot.getInventory().getItems().get(i).getId() != 0) {
                            bot.getInventory().slotClick(bot, i, 1, WindowAction.DROP_ITEM);
                        }
                    }
                }, count.getAndIncrement(), TimeUnit.SECONDS);
            }
        });

        ChatUtil.sendChatMessage("&7Боты начали выкидывать предметы.", player, true);
    }
}
