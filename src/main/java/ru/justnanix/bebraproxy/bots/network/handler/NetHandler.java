package ru.justnanix.bebraproxy.bots.network.handler;

import lombok.RequiredArgsConstructor;
import ru.justnanix.bebraproxy.bots.Bot;
import ru.justnanix.bebraproxy.network.packet.Packet;

@RequiredArgsConstructor
public class NetHandler {
    public final Bot bot;

    public void handlePacket(Packet packet) {}
    public void onDisconnect() {}
}
