package ru.justnanix.bebraproxy.network.connection.handler.lobby.overlay;

import java.util.TimerTask;

public class TitleLagThread extends TimerTask {
    public void run() {
        /*BebraProxy.getInstance().getServer().getPlayers().stream()
                .filter(p -> p.isConnectedToRemote() && p.getOptionsManager().getOptionByName("Детектор лагов").asBooleanOption().isEnabled() && p.getLastPacket().getLastMs() > 5000)
                .forEach(p -> PacketUtil.sendTitle(p, "&cСервер лагает", "&c" + p.getLastPacket().getLastMs() + "&cms", 2, 15, 4));
         */
    }
}