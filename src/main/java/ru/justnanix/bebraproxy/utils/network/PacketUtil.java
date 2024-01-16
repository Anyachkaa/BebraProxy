package ru.justnanix.bebraproxy.utils.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.api.chat.ComponentBuilder;
import ru.justnanix.bebraproxy.network.data.playerlist.PlayerListEntry;
import ru.justnanix.bebraproxy.network.data.playerlist.PlayerListEntryAction;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.ProtocolType;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.data.*;
import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.network.packet.impl.CustomPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.*;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

import java.util.UUID;
import java.util.stream.IntStream;

import static ru.justnanix.bebraproxy.network.packet.Packet.Builder.DataType.*;

public class PacketUtil {
    private static final UUID bossBarUUID = UUID.randomUUID();
    private static final int bossBarID = 999;

    private static int spawnEntityID = 1;

    public static PacketBuffer createEmptyPacketBuffer() {
        return new PacketBuffer(Unpooled.buffer());
    }

    public static CustomPacket createCustomPacket(int id, Packet.Builder.CustomData... data) {
        return new Packet.Builder().init(data).build(id);
    }

    public static void onChangeServer(ProxiedPlayer player) {
        PacketUtil.clearTabList(player);
    }

    public static void clearTabList(ProxiedPlayer player) {
        player.getConnectMgr().sendPacket(new ServerPlayerListEntryPacket(PlayerListEntryAction.REMOVE_PLAYER,
                player.getTabList().toArray(new PlayerListEntry[0])));
        player.getTabList().clear();
    }

    public static void setDisplayName(ProxiedPlayer player, String name) {
        PlayerListEntry playerListEntry = new PlayerListEntry(player.getGameProfile(), Gamemode.ADVENTURE, 0, new ComponentBuilder(name).create());
        player.getConnectMgr().sendPacket(new ServerPlayerListEntryPacket(PlayerListEntryAction.UPDATE_DISPLAY_NAME, new PlayerListEntry[]{playerListEntry}));
    }

    public static void resetDisplayName(ProxiedPlayer player) {
        PlayerListEntry playerListEntry = new PlayerListEntry(player.getGameProfile(), Gamemode.ADVENTURE, 0, null);
        player.getConnectMgr().sendPacket(new ServerPlayerListEntryPacket(PlayerListEntryAction.UPDATE_DISPLAY_NAME, new PlayerListEntry[]{playerListEntry}));
    }

    public static byte[] copyBuff(ByteBuf in) {
        ByteBuf incopy = in.duplicate();

        byte[] customData = new byte[incopy.readableBytes()];
        incopy.readBytes(customData);
        incopy.clear();

        return customData;
    }

    public static void fly(ProxiedPlayer player, boolean fly) {
        player.getConnectMgr().sendPacket(new ServerPlayerAbilitiesPacket(false, fly, fly, fly, 0.1f, 0.1f));
    }

    public static void speed(ProxiedPlayer player, float speed) {
        player.getConnectMgr().sendPacket(new ServerPlayerAbilitiesPacket(false, false, false, false, 1.0f, speed));
    }

    public static void lobbyPosTeleport(ProxiedPlayer player) {
        player.getConnectMgr().sendPacket(new ServerPlayerPosLookPacket(0.5, 70, 0.5, 0.0f, 0.0f));
    }

    public static void clearInventory(ProxiedPlayer player) {
        IntStream.range(0, 45).forEach(i -> player.getConnectMgr().sendPacket(new ServerSetSlotPacket(0, i, null)));
    }

    public static void changeGameMode(ProxiedPlayer player, Gamemode gamemode) {
        player.getConnectMgr().sendPacket(new ServerChangeGameStatePacket(new Effect(3, gamemode.getId())));
        switch (gamemode) {
            case SURVIVAL:
            case ADVENTURE:
                player.getConnectMgr().sendPacket(new ServerPlayerAbilitiesPacket(true, false, false, false, 0, 0));
                break;
            case CREATIVE:
                player.getConnectMgr().sendPacket(new ServerPlayerAbilitiesPacket(false, true, true, true, 0.1f, 0.1f));
                break;
            case SPECTATOR:
                player.getConnectMgr().sendPacket(new ServerPlayerAbilitiesPacket(false, false, true, false, 0.1f, 0.1f));
                break;
        }
    }

    public static void sendTitle(ProxiedPlayer player, String header, String footer) {
        sendTitle(player, header, footer, 10, 20, 10);
    }

    public static void sendTitle(ProxiedPlayer player, String header, String footer, int fadeIn, int stay, int fadeOut) {
        if (header != null) {
            player.getConnectMgr().sendPacket(new ServerTitlePacket(TitleAction.TITLE, ChatUtil.fixColor(header)));
        }
        if (footer != null) {
            player.getConnectMgr().sendPacket(new ServerTitlePacket(TitleAction.SUBTITLE, ChatUtil.fixColor(footer)));
        }
        player.getConnectMgr().sendPacket(new ServerTitlePacket(TitleAction.TIMES, fadeIn, stay, fadeOut));
    }

    public static void sendActionBar(String message, ProxiedPlayer player) {
        if (player.getConnectMgr().getPacketCodec().getProtocol() == ProtocolType.PROTOCOL_1_12_2.getProtocol()) {
            player.getConnectMgr().sendPacket(new ServerTitlePacket(TitleAction.ACTIONBAR, ChatUtil.fixColor(message)));
        } else {
            player.getConnectMgr().sendPacket(new ServerChatPacket(ChatUtil.fixColor(message), MessagePosition.HOTBAR));
        }
    }

    public static void spawnParticle(ProxiedPlayer player, int particleID, boolean longDistance, Position pos, float offsetX,
                                     float offsetY, float offsetZ, float particleData, int particleCount) {
        Packet.Builder particlePacket = new Packet.Builder()
                .add(INT, particleID)
                .add(BOOLEAN, longDistance)
                .add(FLOAT, (float) pos.getX())
                .add(FLOAT, (float) pos.getY())
                .add(FLOAT, (float) pos.getZ())
                .add(FLOAT, offsetX)
                .add(FLOAT, offsetY)
                .add(FLOAT, offsetZ)
                .add(FLOAT, particleData)
                .add(INT, particleCount);

        if (player.getConnectMgr().getPacketCodec().getProtocol() == ProtocolType.PROTOCOL_1_12_2.getProtocol()) {
            player.getConnectMgr().sendPacket(particlePacket.build(0x22));
        }
    }
}