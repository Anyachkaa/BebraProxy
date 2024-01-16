package ru.justnanix.bebraproxy.network.data.playerlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import net.md_5.bungee.api.chat.BaseComponent;
import ru.justnanix.bebraproxy.network.data.GameProfile;
import ru.justnanix.bebraproxy.network.data.Gamemode;

@Data
@AllArgsConstructor
@ToString
public class PlayerListEntry {
    private GameProfile profile;
    private Gamemode gameMode;
    private int ping;
    private BaseComponent[] displayName;

    public PlayerListEntry(GameProfile profile, Gamemode gameMode) {
        this.profile = profile;
        this.gameMode = gameMode;
    }

    public PlayerListEntry(GameProfile profile, int ping) {
        this.profile = profile;
        this.ping = ping;
    }

    public PlayerListEntry(GameProfile profile, BaseComponent displayName) {
        this.profile = profile;
        this.displayName = new BaseComponent[]{displayName};
    }

    public PlayerListEntry(GameProfile profile) {
        this.profile = profile;
    }
}

