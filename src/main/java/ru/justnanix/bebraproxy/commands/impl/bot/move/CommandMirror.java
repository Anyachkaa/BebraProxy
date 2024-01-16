package ru.justnanix.bebraproxy.commands.impl.bot.move;

import lombok.Getter;
import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.bots.Bot;
import ru.justnanix.bebraproxy.bots.mirror.MirrorRecord;
import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;
import ru.justnanix.bebraproxy.utils.proxy.ThreadUtils;
import ru.justnanix.bebraproxy.utils.proxy.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Command.CommandInfo(
        name = "mirror",
        desc = "Боты повторяют ваши действия",
        usage = "[on/off] | delay [мс]",
        allowedStates = ConnectionInfo.REMOTE)
@Getter
public class CommandMirror extends Command {
    private final List<MirrorRecord> records = new ArrayList<>();
    private MirrorRecord currentRecord;
    private boolean active = false;
    private int delay = 200;

    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        CommandMacro commandMacro = player.getCommandManager().getCommandByClass(CommandMacro.class);
        if (commandMacro.isRecording() || commandMacro.isPlaying()) {
            ChatUtil.sendChatMessage("&cОстановите запись/проигрывание макроса!", player, true);
            return;
        }

        if (args[0].equalsIgnoreCase("on")) {
            if (active)
                return;

            active = true;
            records.clear();

            new Thread(() -> {
                Timer timer = new Timer(20.0F);

                while (player.isConnectedToProxy() && player.isConnectedToRemote() && active) {
                    boolean recordTick = true;

                    for (int i = 0; i < Math.min(10, timer.elapsedTicks); ++i) {
                        if (recordTick)
                            currentRecord = new MirrorRecord(player.getPosition(), new ArrayList<>());
                        else records.add(currentRecord);

                        recordTick = !recordTick;
                    }

                    ThreadUtils.sleep(1L);
                }
            }).start();

            ChatUtil.sendChatMessage("&7Включён &cMother", player, true);

            int count = 0;
            for (Bot bot : player.getBotManager().getBots()) {
                BebraProxy.simpleTasks.schedule(() -> {
                    if (!active) return;
                    bot.setMirror(true);
                }, (long) count * delay, TimeUnit.MILLISECONDS);

                count++;
            }
        } else if (args[0].equalsIgnoreCase("off")) {
            active = false;
            for (Bot bot : player.getBotManager().getBots()) {
                bot.setMirror(false);
                bot.setMirrorIndex(0);
            }

            BebraProxy.simpleTasks.schedule(() -> {
                records.clear();
                currentRecord = null;
                ChatUtil.sendChatMessage("&7Выключен &cMother", player, true);
            }, 250L, TimeUnit.MILLISECONDS);
        } else if(args[0].equalsIgnoreCase("delay")) {
            delay = Integer.parseInt(args[1]);
            ChatUtil.sendChatMessage("&7Поставлена задержка на &c" + delay + "мс", player, true);
        }
    }
}
