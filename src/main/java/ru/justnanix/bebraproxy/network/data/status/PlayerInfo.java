package ru.justnanix.bebraproxy.network.data.status;

import lombok.Data;
import ru.justnanix.bebraproxy.network.data.GameProfile;

@Data
public class PlayerInfo {
    private final int onlinePlayers, maxPlayers;
    private final GameProfile[] players;
}