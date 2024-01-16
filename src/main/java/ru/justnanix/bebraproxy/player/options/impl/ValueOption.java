package ru.justnanix.bebraproxy.player.options.impl;

import lombok.Getter;
import lombok.Setter;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.player.options.Option;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerCloseWindowPacket;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

@Getter
public class ValueOption extends Option {
    @Setter
    private String value;

    public ValueOption(Option.Category category, ProxiedPlayer player, String value, String name, String description) {
        super(player, name, description, category);
        this.value = value;
    }

    public void set() {
        if (player.getCurrentGui() != null)
            player.getConnectMgr().sendPacket(new ServerCloseWindowPacket(player.getCurrentGui().windowID));

        ChatUtil.sendChatMessage("Введите в чат новое значение", player, true);
        player.setCurrentValueOption(this);
    }
}
