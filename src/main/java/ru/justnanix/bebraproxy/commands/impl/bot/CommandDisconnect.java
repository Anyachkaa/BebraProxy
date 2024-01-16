package ru.justnanix.bebraproxy.commands.impl.bot;

import ru.justnanix.bebraproxy.bots.Bot;
import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

@Command.CommandInfo(
        name = "disconnect",
        desc = "Отключает ботов",
        usage = "[all/кол-во]")
public class CommandDisconnect extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        if (args[0].equalsIgnoreCase("all")) {
            for (Bot bot : player.getBotManager().getBots()) {
                try {
                    player.getBotManager().getBots().remove(bot);
                    bot.getSession().getChannel().close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            ChatUtil.sendChatMessage("Отключены все боты.", player, true);
        } else {
            int count = Integer.parseInt(args[0]);
            if (count >= player.getBotManager().getBots().size())
                count = player.getBotManager().getBots().size()-1;

            if (count <= 0) {
                ChatUtil.sendChatMessage("Некорректное число", player, true);
                return;
            }

            int i = 0;
            for (Bot bot : player.getBotManager().getBots()) {
                if (i >= count) {
                    break;
                }

                try {
                    player.getBotManager().getBots().remove(bot);
                    bot.getSession().getChannel().close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                i++;
            }

            ChatUtil.sendChatMessage("Отключено " + count + " ботов.", player, true);
        }
    }
}
