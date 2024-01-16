package ru.justnanix.bebraproxy.commands.impl;

import net.md_5.bungee.api.chat.*;
import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerChatPacket;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command.CommandInfo(
        name = "help",
        desc = "Помощь по командам",
        usage = "[страница]")
public class CommandHelp extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) {
        List<Command> availableCommands = new ArrayList<>(player.getCommandManager().getCommands());
        availableCommands.addAll(player.getCommandManager().getBotCommands());
        availableCommands.removeIf(cmd -> !cmd.getAllowedPlans().contains(player.getAccount().getPlan()));

        int i = 1;
        if (args.length == 1) 
            i = Integer.parseInt(args[0]);
        int k = i - 1;
        int l = Math.min(i * 10, availableCommands.size());
        int j = (availableCommands.size() - 1) / 10;
        int f = j + 1;

        if (i < 1 || i > f) {
            ChatUtil.sendChatMessage("&cТакой страницы нет!", player, false);
            return;
        }

        ChatUtil.clearChat(1, player);
        ChatUtil.sendChatMessage("&7&l>>&f&m-----------------------------&r&7&l<<&r\n", player, false);
        ChatUtil.sendChatMessage("&7Помощь &8(&c" + i + "&8/&c" + f + "&8)", player, true);

        String prefixCMD = player.getOptionsManager().getCmdPrefix();
        for (int i1 = k * 10; i1 < l; ++i1) {
            Command command = availableCommands.get(i1);
            ChatUtil.sendChatMessage(new ComponentBuilder()
                    .append(ChatUtil.parseComponent("&c>> " + prefixCMD + (player.getCommandManager().getBotCommands().contains(command) ? "bots " : "")
                            + command.getName() + " &7" + command.getUsage()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(ChatUtil.fixColor(
                            "&7Команда: &c" + command.getName() + "\n" +
                                    "&7Описание: &c" + command.getDesc() + "\n" +
                                    "&7Использование: &c" + player.getOptionsManager().getCmdPrefix() +
                                    (player.getCommandManager().getBotCommands().contains(command) ? "bots " : "")
                                    + command.getName() + " " + command.getUsage() + "\n" +
                                    "&7Разрешенные подключения: &c"
                                    + ((Arrays.equals(command.getAllowedStates().toArray(), ConnectionInfo.values())) ?
                                    "любые" : Arrays.toString(command.getAllowedStates().toArray()))
                                    + " (ваше подключение " + player.getConnectionInfo().name() + ")"
                    ))))
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, prefixCMD
                            + (player.getCommandManager().getBotCommands().contains(command) ? "bots " : "") + command.getName() + " " + command.getUsage()))
                    .create(), player, false);
        }

        ChatUtil.sendChatMessage("", player, false);

        TextComponent msg = new TextComponent(ChatUtil.fixColor(((i != 1) ? "&8[&a<--НАЗАД&8]&r" : "&8[&c<--НАЗАД&8]&r")));
        if (i != 1) {
            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new TextComponent(ChatUtil.fixColor("&7Нажмите чтобы перейти на предыдущую страницу!"))));
            msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, prefixCMD + "help " + k));
        }

        TextComponent msg2 = new TextComponent(ChatUtil.fixColor((i != f ? "&8[&aВПЕРЁД-->&8]&r" : "&8[&cВПЕРЁД-->&8]&r")));
        if (i != f) {
            msg2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new TextComponent(ChatUtil.fixColor("&7Нажмите чтобы перейти на следующую страницу"))));
            msg2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, prefixCMD + "help " + (i + 1)));
        }

        player.getConnectMgr().sendPacket(new ServerChatPacket(msg, new TextComponent(" "), msg2));
        ChatUtil.sendChatMessage("\n&7&l>>&f&m-----------------------------&r&7&l<<&r", player, false);
    }
}