package ru.justnanix.bebraproxy.network.connection.handler.remote;

import ru.justnanix.bebraproxy.network.packet.impl.client.play.ClientKeepAlivePacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.*;
import ru.justnanix.bebraproxy.network.connection.ConnectionManagerRemote;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.network.connection.handler.NetHandler;
import ru.justnanix.bebraproxy.network.packet.Packet;

public class NetHandlerPlayRemote extends NetHandler {
    private final ConnectionManagerRemote connectMgr;

    public NetHandlerPlayRemote(ConnectionManagerRemote connectMgr) {
        super(connectMgr.getPlayer());
        this.connectMgr = connectMgr;
    }

    @Override
    public void initHandler() {
        ConnectionManagerRemote prev = player.getRemoteConnectMgr();
        player.setConnectionInfo(ConnectionInfo.REMOTE);
        player.setRemoteConnectMgr(connectMgr);

        if (prev != null && prev.isChannelOpen()) {
            prev.getChannel().close();
        }
    }

    @Override
    public void onRemoteServerPacket(Packet packet) {
        if (packet instanceof ServerCustomPayloadPacket) {
            ServerCustomPayloadPacket p = (ServerCustomPayloadPacket) packet;
            if (p.getChannel().equals("MC|Brand")) {
                return;
            }
        }

        if (packet instanceof ServerPlayerListHeaderFooter && !player.getOptionsManager().getOptionByName("Таблист сервера").asBooleanOption().isEnabled())
            return;
        if ((packet instanceof ServerDisplayScoreboardPacket
                || packet instanceof ServerScoreboardObjectivePacket
                || packet instanceof ServerUpdateScorePacket)
                && player.getOptionsManager().getOptionByName("Scoreboard").asBooleanOption().isEnabled())
            return;
        player.getConnectMgr().sendPacket(packet);
    }

    @Override
    public void onClientPacket(Packet packet) {
        connectMgr.sendPacket(packet);
    }
}
