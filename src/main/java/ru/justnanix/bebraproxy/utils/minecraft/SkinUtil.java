package ru.justnanix.bebraproxy.utils.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.kbrewster.mojangapi.MojangAPI;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.data.GameProfile;
import ru.justnanix.bebraproxy.network.data.Gamemode;
import ru.justnanix.bebraproxy.network.data.Skin;
import ru.justnanix.bebraproxy.network.data.playerlist.PlayerListEntry;
import ru.justnanix.bebraproxy.network.data.playerlist.PlayerListEntryAction;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerPlayerListEntryPacket;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.UUID;

public class SkinUtil {
    public static void showSkin(ProxiedPlayer player) {
        if (player.getGameProfile().getSkin() == null)
            return;

        PlayerListEntry playerListEntry = new PlayerListEntry(player.getGameProfile(), Gamemode.ADVENTURE, 0, null);
        player.getConnectMgr().sendPacket(new ServerPlayerListEntryPacket(PlayerListEntryAction.ADD_PLAYER, new PlayerListEntry[]{playerListEntry}));
        player.getTabList().add(playerListEntry);
    }

    public static void setSkin(ProxiedPlayer player) {
        try {
            UUID uuid = MojangAPI.getUUID(player.getAccount().getKeyName());
            GameProfile gameProfile = new GameProfile(uuid, player.getAccount().getKeyName());

            URL url = new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", gameProfile.getUuid()));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();

            String json = new Scanner(connection.getInputStream(), String.valueOf(StandardCharsets.UTF_8)).useDelimiter("\\Z").next();
            JsonObject object = new JsonParser().parse(json).getAsJsonObject();
            JsonArray properties = object.getAsJsonArray("properties");
            JsonObject jsonObject = properties.get(0).getAsJsonObject();

            player.getGameProfile().getProperties()
                    .add(new GameProfile.Property("textures", jsonObject.get("value").getAsString(), jsonObject.get("signature").getAsString()));
            player.getGameProfile().setSkin(new Skin(player.getGameProfile()));
        } catch (Throwable ignored) {}
    }
}