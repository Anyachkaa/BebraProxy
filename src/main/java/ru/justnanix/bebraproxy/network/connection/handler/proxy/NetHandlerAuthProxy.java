package ru.justnanix.bebraproxy.network.connection.handler.proxy;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.data.Difficulty;
import ru.justnanix.bebraproxy.network.data.Dimension;
import ru.justnanix.bebraproxy.network.data.Gamemode;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerCustomPayloadPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerJoinGamePacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerPlayerAbilitiesPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerPlayerPosLookPacket;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.network.connection.handler.NetHandler;
import ru.justnanix.bebraproxy.network.ProtocolType;
import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.network.packet.impl.client.play.ClientChatPacket;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;
import ru.justnanix.bebraproxy.utils.minecraft.SkinUtil;
import ru.justnanix.bebraproxy.utils.minecraft.ScoreboardUtil;

import java.text.SimpleDateFormat;

public class NetHandlerAuthProxy extends NetHandler {
    private int tries = 3;
    
    public NetHandlerAuthProxy(ProxiedPlayer player) {
        super(player);
        this.initHandler();
    }

    public void initHandler() {
        System.out.println("[" + player.getAccount().getKeyName() + "] Подключился!");
        player.setConnectionInfo(ConnectionInfo.AUTH);
        player.init();

        SkinUtil.showSkin(player);

        player.setDimension(Dimension.END);
        player.getConnectMgr().sendPacket(new ServerJoinGamePacket(0, Gamemode.ADVENTURE, Dimension.END,
                Difficulty.PEACEFULL, 1, "default_1_1", false));
        player.getConnectMgr().sendPacket(new ServerPlayerAbilitiesPacket(false, true, true, false,
                0f, 0f));
        player.getConnectMgr().sendPacket(new ServerPlayerPosLookPacket(0, 70, 0, 180, 90));
        player.getConnectMgr().sendPacket(new ServerCustomPayloadPacket("MC|Brand",
                ByteBufUtil.getBytes(new PacketBuffer(Unpooled.buffer()).writeString("BebraProxy"))));

        ScoreboardUtil.sendScoreboard(player);

        ChatUtil.sendChatMessage("&c>> &7Приветствуем на &cBebra&rProxy!", player, false);
        ChatUtil.sendChatMessage("&c>> &7Ваш аккаунт есть в базе и на нем активна подписка.", player, false);
        ChatUtil.sendChatMessage("", player, false);
        ChatUtil.sendChatMessage("&c>> &7Авторизируйтесь, используя команду: &c"
                + "$ login [пароль]", player, false);
    }

    @Override
    public void onRemoteServerPacket(Packet p) {}

    @Override
    public void onClientPacket(Packet p) {
        if (p instanceof ClientChatPacket) {
            ClientChatPacket packet = (ClientChatPacket) p;

            try {
                String pass = packet.getMessage().split("\\$ login")[1].trim();
                if (pass.equals(player.getAccount().getPassword())) {
                    ChatUtil.clearChat(20, player);
                    ChatUtil.sendBroadcastMessage("&c>> &7Юзер &c" + player.getAccount().getKeyName() + " &7подключился &7к &cBebra&rProxy &7(&c" +
                            ProtocolType.getByProtocolID(player.getConnectMgr().getPacketCodec().getProtocol()).getPrefix() + "&7)", false);
                    ChatUtil.sendChatMessage("", player, false);
                    ChatUtil.sendChatMessage("&c>> &7Ваша подписка действует до: &c" + new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss")
                            .format(player.getAccount().getExpires()), player, false);
                    ChatUtil.sendChatMessage("&c>> &7Помощь по командам: &c" + player.getOptionsManager().getCmdPrefix()
                            + "help", player, false);
                    player.getConnectMgr().setNetHandler(new NetHandlerPlayProxy(player));
                    BebraProxy.getInstance().getServer().connectPlayerToLobby(player);
                } else if (tries == 1) {
                    // TODO: Recode that shit (нету блокировки)
                    BebraProxy.getInstance().getServer().disconnectPlayer(player,
                            "§c§lBebra§f§lProxy§r\n\n" +
                                    "§7Вы ввели неправильный пароль слишком много раз!\n" +
                                    "§7Ваш айпи был заблокирован на §c15 минут.");
                } else {
                    ChatUtil.sendChatMessage("&c>> &7Введённый пароль &cневерен. &7(Осталось &c" + --tries + "&7 попыток)", player, false);
                }
            } catch (Throwable e) {
                e.printStackTrace();
                ChatUtil.sendChatMessage("&c>> &7Авторизируйтесь, используя команду: &c$ login [пароль]", player, false);
            }
        }
    }
}
