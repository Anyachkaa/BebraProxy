package ru.justnanix.bebraproxy.commands.impl.admin;

import io.netty.channel.ChannelFutureListener;
import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.player.plan.Plan;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerDisconnectPacket;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

import java.util.Optional;

@Command.CommandInfo(
        name = "kick",
        desc = "Кикает игрока с прокси",
        usage = "[игрок] [причина]",
        allowedPlans = {Plan.ADMIN})
public class CommandKick extends Command {
    @Override
    public void onCommand(ProxiedPlayer sender, String[] args) {
        String reason = "";
        for (int i = 2; i < args.length; ++i) {
            reason = (i != 2 ? reason + " " : "") + args[i];
        }

        Optional<ProxiedPlayer> optionalPlayer = BebraProxy.getInstance().getServer().getPlayer(args[0]);
        if (!optionalPlayer.isPresent()) {
            ChatUtil.sendChatMessage("&cЭтот игрок оффлайн или его не существует!", sender, true);
            return;
        }

        ProxiedPlayer player = optionalPlayer.get();

        if (sender.getAccount().getKeyName().equalsIgnoreCase(args[0])) {
            ChatUtil.sendChatMessage("&cСамого себя нельзя кикнуть!", sender, true);
            return;
        }

        if (player.getAccount().getPlan() == Plan.ADMIN) {
            ChatUtil.sendChatMessage("&cВы не можете кикнуть этого игрока!", sender, true);
            return;
        }

        player.getConnectMgr().sendPacket(new ServerDisconnectPacket(ChatUtil.fixColor(
                "&c&lBebra&f&lProxy" +
                "\n\n&7       Тебя кикнул администратор!" +
                "\n&r&7Причина:&c ") + reason)).addListener(ChannelFutureListener.CLOSE);
        ChatUtil.sendChatMessage("&7Вы успешно кикнули игрока с прокси. Его никнейм: &c" + args[0], sender, true);
    }
}