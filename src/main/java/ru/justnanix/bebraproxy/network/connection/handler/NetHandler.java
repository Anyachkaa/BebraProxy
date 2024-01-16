package ru.justnanix.bebraproxy.network.connection.handler;

import lombok.RequiredArgsConstructor;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.packet.Packet;

@RequiredArgsConstructor
public abstract class NetHandler {
    protected final ProxiedPlayer player;

    public void initHandler() {}

    public abstract void onRemoteServerPacket(Packet packet);
    public abstract void onClientPacket(Packet packet);
}
