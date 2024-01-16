package ru.justnanix.bebraproxy.commands.impl.bot.move;

import lombok.Getter;
import lombok.Setter;
import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.bots.Bot;
import ru.justnanix.bebraproxy.bots.macro.Macro;
import ru.justnanix.bebraproxy.bots.macro.MacroRecord;
import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;
import ru.justnanix.bebraproxy.utils.proxy.ThreadUtils;
import ru.justnanix.bebraproxy.utils.proxy.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Command.CommandInfo(
        name = "macro",
        desc = "Макросы для ботов",
        usage = "play [имя макроса] | rec [имя макроса] | delay [задержка] | stop | list",
        allowedStates = ConnectionInfo.REMOTE)
@Getter
public class CommandMacro extends Command {
    private final List<Macro> macros = new ArrayList<>();
    
    private Macro currentMacro;
    @Setter private MacroRecord currentRecord;
    private boolean playing = false, recording = false;
    private int delay = 200;

    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        if (!args[0].equalsIgnoreCase("list") && player.getCommandManager().getCommandByClass(CommandMirror.class).isActive()) {
            ChatUtil.sendChatMessage("&cВыключите Mother!", player, true);
            return;
        }

        if (args[0].equalsIgnoreCase("rec")) {
            if (recording || playing) {
                ChatUtil.sendChatMessage("&cОстановите запись/проигрывание макроса!", player, true);
                return;
            }

            String name = args[1];

            if (macros.stream().anyMatch(macro -> macro.getName().equals(name))) {
                ChatUtil.sendChatMessage("&cУже есть такой макрос!", player, true);
                return;
            }

            recording = true;
            currentMacro = new Macro(args[1], new ArrayList<>());
            currentRecord = new MacroRecord(new ArrayList<>());

            ChatUtil.sendChatMessage("Начинаю записывать макрос &c" + args[1], player, true);
            
            new Thread(() -> {
                Timer timer = new Timer(20.0F);
                
                while (player.isConnectedToProxy() && player.isConnectedToRemote() && recording) {
                    for (int i = 0; i < Math.min(10, timer.elapsedTicks); ++i) {
                        double xChange = player.getPosition().getX() - player.getPrevPosition().getX();
                        double yChange = player.getPosition().getY() - player.getPrevPosition().getY();
                        double zChange = player.getPosition().getZ() - player.getPrevPosition().getZ();

                        currentRecord.setPosChange(new MacroRecord.PosChange(xChange, yChange, zChange));
                        currentMacro.getRecords().add(currentRecord);
                        currentRecord = new MacroRecord(new ArrayList<>());
                    }
                    
                    ThreadUtils.sleep(1L);
                }
            }).start();
        } else if (args[0].equalsIgnoreCase("stop")) {
            if (recording) {
                recording = false;
                macros.add(currentMacro);

                ChatUtil.sendChatMessage("Записан макрос &c" + currentMacro.getName(), player, true);

                currentMacro = null;
            } else if (playing) {
                for (Bot bot : player.getBotManager().getBots()) {
                    bot.setMacro(false);
                    bot.setMacroComplete(false);
                    bot.setMacroIndex(0);
                }

                ChatUtil.sendChatMessage("Остановлено проигрывание макроса &c" + currentMacro.getName(), player, true);

                playing = false;
                currentMacro = null;
            } else {
                ChatUtil.sendChatMessage("&cСейчас не записывается/проигрывается ни одного макроса", player, true);
            }
        } else if (args[0].equalsIgnoreCase("play")) {
            if (recording || playing) {
                ChatUtil.sendChatMessage("&cОстановите запись/проигрывание макроса!", player, true);
                return;
            }

            String name = args[1];
            Optional<Macro> opt = macros.stream()
                    .filter(macro -> macro.getName().equals(name))
                    .findFirst();

            if (opt.isPresent()) {
                playing = true;
                currentMacro = (opt.get());

                ChatUtil.sendChatMessage("Начинаю проигрывать макрос &c" + currentMacro.getName(), player, true);

                int count = 0;
                for (Bot bot : player.getBotManager().getBots()) {
                    BebraProxy.simpleTasks.schedule(() -> bot.setMacro(true), (long) count * delay, TimeUnit.MILLISECONDS);
                    count++;
                }

                new Runnable() {
                    final ScheduledFuture<?> future;
                    {
                        future = BebraProxy.simpleTasks.scheduleAtFixedRate(this, 500L, 500L, TimeUnit.MILLISECONDS);
                    }

                    @Override
                    public void run() {
                        if (!player.isConnectedToProxy()) {
                            future.cancel(true);
                        }

                        boolean compl = true;
                        for (Bot bot : player.getBotManager().getBots()) {
                            if (!bot.isMacroComplete()) {
                                compl = false;
                                future.cancel(true);
                            }
                        }

                        if (compl) {
                            for (Bot bot : player.getBotManager().getBots()) {
                                bot.setMacro(false);
                                bot.setMacroComplete(false);
                                bot.setMacroIndex(0);
                            }

                            ChatUtil.sendChatMessage("Все боты проиграли макрос &c" + currentMacro.getName(), player, true);

                            playing = false;
                            currentMacro = null;
                            future.cancel(true);
                        }
                    }
                };
            } else {
                ChatUtil.sendChatMessage("&cТакого макроса не найдено", player, true);
            }
        } else if (args[0].equalsIgnoreCase("delay")) {
            delay = Integer.parseInt(args[1]);
            ChatUtil.sendChatMessage("Задержка установлена на &c" + delay + "мс", player, true);
        } else if (args[0].equalsIgnoreCase("list")) {
            ChatUtil.sendChatMessage("Макросы:", player, true);
            for (Macro macro : macros) {
                ChatUtil.sendChatMessage("&c>> &7" + macro.getName(), player, false);
            }
        }
    }
}
