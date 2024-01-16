package ru.justnanix.bebraproxy.commands.impl.bot.move;

import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

@Command.CommandInfo(
        name = "rotation",
        desc = "Меняет положение головы ботов",
        usage = "[yaw] [pitch]")
public class CommandRotation extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        int yaw = Integer.parseInt(args[0]);
        int pitch = Integer.parseInt(args[1]);

        if (yaw > 180 || yaw < -180 || pitch > 90 || pitch < -90) {
            ChatUtil.sendChatMessage("&cВы неправильно указали yaw или pitch!", player, true);
            return;
        }

        player.getBotManager().getBots().forEach(bot -> {
            bot.setYaw(yaw);
            bot.setPitch(pitch);
        });

        ChatUtil.sendChatMessage("Боты изменили расположение головы.", player, true);
    }
}
