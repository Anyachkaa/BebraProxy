package ru.justnanix.bebraproxy.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;
import ru.justnanix.bebraproxy.utils.proxy.ReflectionUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Getter
public class CommandManager {
    private final List<Command> allCommands = new CopyOnWriteArrayList<>();
    private final List<Command> commands = new CopyOnWriteArrayList<>();
    private final List<Command> botCommands = new CopyOnWriteArrayList<>();

    private final ProxiedPlayer player;
    private long lastCoolDownTimeMs = System.currentTimeMillis();

    public void init() {
        commands.addAll(ReflectionUtil.getClasses("ru.justnanix.bebraproxy.commands.impl", Command.class));
        commands.removeIf(cmd -> cmd.getClass().getPackage().getName().contains("bot"));
        botCommands.addAll(ReflectionUtil.getClasses("ru.justnanix.bebraproxy.commands.impl.bot", Command.class));

        commands.sort(Comparator.comparing(Command::getName));
        botCommands.sort(Comparator.comparing(Command::getName));

        allCommands.addAll(commands);
        allCommands.addAll(botCommands);
    }

    public void onCommand(String message) {
        String prefixCMD = player.getOptionsManager().getCmdPrefix();
        String[] args = message.substring(prefixCMD.length()).split(" ");

        Optional<Command> optionalCommand;
        if (message.startsWith(prefixCMD + "bots") && args.length > 1)
            optionalCommand = botCommands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(args[1])).findAny();
        else optionalCommand = commands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(args[0])).findAny();

        if (!optionalCommand.isPresent()) {
            ChatUtil.sendChatMessage("&cКоманда не найдена", player, true);
            return;
        }

        Command command = optionalCommand.get();
        if (!command.getAllowedPlans().contains(player.getAccount().getPlan())) {
            ChatUtil.sendChatMessage("&cЭта команда вам не доступна.", player, true);
            return;
        }

        if (!command.getAllowedStates().contains(player.getConnectionInfo())) {
            ChatUtil.sendChatMessage("&cВаше текущее подключение (" + player.getConnectionInfo() +
                    ") не соответствует требованиям команды", player, true);
            ChatUtil.sendChatMessage("&cРазрешенные подключения команды: " +
                    Arrays.toString(command.getAllowedStates().stream().map(Enum::name).toArray()), player, true);
            return;
        }

        long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastCoolDownTimeMs);
        if (player.getAccount().getPlan().getDelayCMD() > secondsLeft) {
            long secondsNeed = player.getAccount().getPlan().getDelayCMD() - secondsLeft;
            String secondsNeedString = String.valueOf(secondsNeed);
            String seconds = "";
            switch (secondsNeedString.charAt(secondsNeedString.length()-1)) {
                case '0':
                case '8':
                case '7':
                case '6':
                case '5':
                case '9':
                    seconds = "секунд";
                    break;
                case '1':
                    seconds = "секунду";
                    break;
                case '2':
                case '4':
                case '3':
                    seconds = "секунды";
                    break;
            }

            ChatUtil.sendChatMessage("&7Подождите &c" + secondsNeed + " &7" + seconds + " перед использованием команды.", player, true);
            return;
        } else {
            lastCoolDownTimeMs = System.currentTimeMillis();
        }

        BebraProxy.simpleTasks.execute(() -> {
            try {
                command.onCommand(player, botCommands.contains(optionalCommand.get()) ?
                        Arrays.copyOfRange(args, 2, args.length) : Arrays.copyOfRange(args, 1, args.length));
            } catch (Exception e) {
                e.printStackTrace();
                ChatUtil.sendChatMessage("&7Использование: &c" + prefixCMD + (message.startsWith(prefixCMD + "bots") ? "bots " : "")
                        + command.getName() + " " + command.getUsage().replace(" ", " &c"), player, true);
            }
        });
    }

    public <T> T getCommandByClass(Class<? extends T> clazz) {
        return (T) allCommands.stream()
                .filter(command -> command.getClass() == clazz)
                .findAny().orElse(null);
    }
}