package ru.justnanix.bebraproxy.network.connection.handler.lobby;

import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.network.packet.impl.client.play.ClientChatPacket;
import ru.justnanix.bebraproxy.network.packet.impl.client.play.ClientPlayerTryUseItemPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerCustomPayloadPacket;
import ru.justnanix.bebraproxy.network.connection.ConnectionManagerRemote;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.network.connection.handler.NetHandler;
import ru.justnanix.bebraproxy.network.connection.handler.lobby.ui.gui.GuiOptions;
import ru.justnanix.bebraproxy.utils.minecraft.TabUtil;
import ru.justnanix.bebraproxy.utils.minecraft.ScoreboardUtil;

public class NetHandlerPlayLobby extends NetHandler {
    private final ConnectionManagerRemote connectMgr;

    public NetHandlerPlayLobby(ConnectionManagerRemote connectMgr) {
        super(connectMgr.getPlayer());
        this.connectMgr = connectMgr;
    }

    @Override
    public void initHandler() {
        player.setConnectionInfo(ConnectionInfo.LOBBY);
        TabUtil.updateTab(player);
        ScoreboardUtil.updateScoreboard(player);
    }

    @Override
    public void onRemoteServerPacket(Packet packet) {
        if (packet instanceof ServerCustomPayloadPacket) {
            return;
        }

        player.getConnectMgr().sendPacket(packet);
    }

    @Override
    public void onClientPacket(Packet packet) {
        if (packet instanceof ClientPlayerTryUseItemPacket) {
            if (player.getCurrentSlot() == 8) {
                player.setCurrentGui(new GuiOptions(player));
                player.getCurrentGui().onOpen();
            }
        }

        if (packet instanceof ClientChatPacket)
            return;

        connectMgr.sendPacket(packet);
    }
}
