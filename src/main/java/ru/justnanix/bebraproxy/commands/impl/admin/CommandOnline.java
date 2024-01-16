package ru.justnanix.bebraproxy.commands.impl.admin;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.player.plan.Plan;
import ru.justnanix.bebraproxy.network.ProtocolType;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

@Command.CommandInfo(
        name = "online",
        desc = "Показывает список игроков на прокси и доступные действия с ними.",
        allowedPlans = {Plan.ADMIN})
public class CommandOnline extends Command {
    @Override
    public void onCommand(ProxiedPlayer sender, String[] args) throws Exception {
        BebraProxy.getInstance().getServer().getPlayers().forEach(player -> {
            TextComponent actions = (TextComponent) new TextComponent(ChatUtil.fixColor("&7(&f" +
                    ProtocolType.getByProtocolID(player.getConnectMgr().getPacketCodec().getProtocol()).getPrefix() +
                    "&7) &7[" + player.getAccount().getPlan().getPrefix() + "&7] &c" + player.getAccount().getKeyName()))
                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(ChatUtil.fixColor("Сервер:&7 "
                            + (player.getConnectionInfo() != ConnectionInfo.REMOTE ? "&7Не подключен"
                            : player.getRemoteConnectMgr().getRemoteServerInfo().getHost())))));
            actions.addExtra(new TextComponent(ChatUtil.fixColor("&7[Кик]"))
                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new TextComponent(ChatUtil.fixColor("&fКликни чтобы кикнуть&7 " + player.getAccount().getKeyName()))))
                    .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                            sender.getOptionsManager().getCmdPrefix() + "kick " + player.getAccount().getKeyName() + " reason")));

            ChatUtil.sendChatMessage(new BaseComponent[]{actions}, sender, false);
        });
    }
}