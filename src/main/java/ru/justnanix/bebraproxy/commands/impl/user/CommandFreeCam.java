package ru.justnanix.bebraproxy.commands.impl.user;

import lombok.Getter;
import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.network.data.Gamemode;
import ru.justnanix.bebraproxy.network.data.Position;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerPlayerPosLookPacket;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;
import ru.justnanix.bebraproxy.utils.network.PacketUtil;

@Command.CommandInfo(
        name = "freecam",
        desc = "Включает режим свободной камеры, для сервера вы стоите на месте",
        allowedStates = ConnectionInfo.REMOTE)
public class CommandFreeCam extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        player.setFreeCam(!player.isFreeCam());
        ChatUtil.sendChatMessage("&7Свободная камера успешно " +
                (player.isFreeCam() ? "&aвключена" : "&cвыключена") + "&7!", player, true);

        if (player.isFreeCam()) {
            PacketUtil.changeGameMode(player, Gamemode.SPECTATOR);
        } else {
            Position position = player.getPosition();
            player.getConnectMgr().sendPacket(new ServerPlayerPosLookPacket(
                    position.getX(), position.getY(), position.getZ(),
                    position.getYaw(), position.getPitch())
            );
            PacketUtil.changeGameMode(player, Gamemode.SURVIVAL);
        }
    }
}
