package ru.justnanix.bebraproxy.commands.impl.bot.crash;

import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

@Command.CommandInfo(
        name = "crash",
        desc = "Нагружает сервер путём массового отправления нестандартных пакетов ботами",
        usage = "[имя крашера] | list")
public class CommandCrash extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        ChatUtil.sendChatMessage("Данная команда в разработке.", player, true);

        /*int limit = 19999;

        Optional<Crasher> crasher;
        if (args[0].equalsIgnoreCase("list")) {
            ChatUtil.sendChatMessage("Список крашеров: ", player, true);

            for (Crasher crash : BebraProxy.getInstance().getCrasherManager().elements) {
                ChatUtil.sendChatMessage(">> &c" + crash.getName() + " &7" + crash.getArguments(), player, false);
            }
        } else if ((crasher = BebraProxy.getInstance().getCrasherManager().findExploit(args[0])).isPresent()) {
            if (Integer.parseInt(args[1]) > limit) {
                ChatUtil.sendChatMessage("[&c" + crasher.get().getName().toUpperCase() + "&7] Вы указали слишком большое число.", player, true);
                return;
            }
            ChatUtil.sendChatMessage("[&c" + crasher.get().getName().toUpperCase() + "&7] Запуск...", player, true);

            try {
                crasher.get().execute(player, Arrays.copyOfRange(args, 1, args.length));
            } catch (Throwable e) {
                e.printStackTrace();
                ChatUtil.sendChatMessage("&cВведите аргументы крашера правильно!", player, true);
            }
        } else {
            ChatUtil.sendChatMessage("&cТакой крашер не найден", player, true);
        }
         */
    }
}
