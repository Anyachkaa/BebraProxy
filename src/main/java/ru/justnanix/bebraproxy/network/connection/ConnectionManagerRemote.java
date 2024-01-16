package ru.justnanix.bebraproxy.network.connection;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.Setter;
import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.network.ProxyServer;
import ru.justnanix.bebraproxy.network.codec.PacketCodec;
import ru.justnanix.bebraproxy.network.codec.VarInt21FrameCodec;
import ru.justnanix.bebraproxy.network.data.EnumConnectionState;
import ru.justnanix.bebraproxy.network.data.GameProfile;
import ru.justnanix.bebraproxy.network.data.playerlist.PlayerListEntry;
import ru.justnanix.bebraproxy.network.data.playerlist.PlayerListEntryAction;
import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.network.packet.PacketDirection;
import ru.justnanix.bebraproxy.network.packet.impl.CustomPacket;
import ru.justnanix.bebraproxy.network.packet.impl.client.HandshakePacket;
import ru.justnanix.bebraproxy.network.packet.impl.client.login.ClientLoginStartPacket;
import ru.justnanix.bebraproxy.network.packet.impl.client.play.ClientKeepAlivePacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.login.ServerLoginDisconnectPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.login.ServerLoginSetCompressionPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.login.ServerLoginSuccessPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.*;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.handler.NetHandler;
import ru.justnanix.bebraproxy.network.connection.handler.lobby.NetHandlerPlayLobby;
import ru.justnanix.bebraproxy.network.connection.handler.remote.NetHandlerPlayRemote;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;
import ru.justnanix.bebraproxy.utils.minecraft.WorldUtil;
import ru.justnanix.bebraproxy.utils.network.PacketUtil;

@Getter
@Setter
public class ConnectionManagerRemote extends ConnectionManager {
    private static final AttributeKey<String> disconnectReason = AttributeKey.newInstance("disconnectReason");

    private final ProxiedPlayer player;
    private NetHandler netHandler;

    private ServerInfo remoteServerInfo;
    private GameProfile remoteGameProfile;

    public ConnectionManagerRemote(Channel channel, ProxiedPlayer player) {
        super(channel);
        this.player = player;
    }

    public void connectToRemote(String name, ServerInfo info) {
        ConnectionInfo prevInfo = player.getConnectionInfo();
        player.setConnectionInfo(ConnectionInfo.CONNECTING);
        remoteServerInfo = info;
        remoteGameProfile = new GameProfile((String) null, name);

        PacketUtil.sendTitle(player, ChatUtil.fixColor("&cПодключаюсь к " + remoteServerInfo.getHost() + ":" + remoteServerInfo.getPort() + "..."),
                String.format("&7%s:%d", remoteServerInfo.getHost(), remoteServerInfo.getPort()));
        ChatUtil.sendChatMessage("&7Подключаюсь к серверу &c" + remoteServerInfo.getHost() + ":" + remoteServerInfo.getPort(), player, true);
        this.setNetHandler(new NetHandlerPlayRemote(this));

        this.createBootstrapAndConnect(remoteServerInfo.getHost(), remoteServerInfo.getPort())
                .addListener((ChannelFuture future) -> {
                    if (!future.isSuccess()) {
                        PacketUtil.sendTitle(player, "&cНе удалось подключиться", "&7" + future.channel().attr(disconnectReason).get(),
                                10, 20, 10);
                        ChatUtil.sendChatMessage("&cНе удалось подключиться к серверу: "
                                + future.cause().getClass().getName() + ": " + future.cause().getMessage(), player, true);

                        if (player.getConnectionInfo() == ConnectionInfo.CONNECTING) {
                            player.setConnectionInfo(prevInfo);
                        }
                    }
                }).channel().closeFuture().addListener((ChannelFuture future) -> {
                    if (player.getRemoteConnectMgr() == this || future.channel().attr(disconnectReason).get() != null) {
                        PacketUtil.sendTitle(player, "&cОтключение", "&7" + future.channel().attr(disconnectReason).get(),
                                10, 20, 10);
                        ChatUtil.sendChatMessage("Отключился от сервера: &c"
                                + future.channel().attr(disconnectReason).get(), player, true);

                        if (player.getConnectionInfo() == ConnectionInfo.REMOTE) {
                            ChatUtil.sendChatMessage("&cОтправляем вас в лобби...", player, true);
                            BebraProxy.getInstance().getServer().connectPlayerToLobby(player);
                        } else if (player.getConnectionInfo() == ConnectionInfo.CONNECTING) {
                            player.setConnectionInfo(prevInfo);
                        }
                    }
                });
    }

    public void connectToLobby() {
        player.setConnectionInfo(ConnectionInfo.CONNECTING);
        ConnectionManagerRemote prev = player.getRemoteConnectMgr();
        player.setRemoteConnectMgr(this);
        if (prev != null && prev.isChannelOpen())
            prev.getChannel().close();

        String host = "localhost";
        int port = 2024;
        remoteGameProfile = new GameProfile(player.getGameProfile().getUuid(), player.getGameProfile().getName());
        remoteServerInfo = new ServerInfo(host, port);

        ChatUtil.sendChatMessage("&7Перемещаем вас в лобби...", player, true);
        this.setNetHandler(new NetHandlerPlayLobby(this));

        this.createBootstrapAndConnect(host, port).channel().closeFuture()
                .addListener((ChannelFuture future) -> {
                    if (player.getRemoteConnectMgr() == this) {
                        ChatUtil.sendChatMessage("Отключился от лобби: &c"
                                + future.channel().attr(disconnectReason).get(), player, true);

                        WorldUtil.limboWorld(player);
                        player.setRemoteConnectMgr(null);
                    }
                });
    }

    private ChannelFuture createBootstrapAndConnect(String host, int port) {
        return new Bootstrap()
                .group(ProxyServer.nettyGroup)
                .channel(ProxyServer.nettyChannel)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        channel.pipeline()
                            .addLast("timeout", new ReadTimeoutHandler(30))
                            .addLast("frameCodec", new VarInt21FrameCodec())
                            .addLast("packetCodec", new PacketCodec(EnumConnectionState.LOGIN, PacketDirection.SERVERBOUND, channel))
                            .addLast("handler", ConnectionManagerRemote.this);
                    }
                }).connect(host, port).addListener((ChannelFuture future) -> {
                    if (!future.isSuccess()) {
                        future.channel().attr(disconnectReason).set(future.cause().getClass().getName() + ": " + future.cause().getMessage());
                    }
                });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
        this.getPacketCodec().setProtocol(player.getConnectMgr().getPacketCodec().getProtocol());
        this.sendPacket(new HandshakePacket(this.getPacketCodec().getProtocol(),
                remoteServerInfo.getHost(), remoteServerInfo.getPort(), 2));
        this.sendPacket(new ClientLoginStartPacket(remoteGameProfile.getName()));
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ServerJoinGamePacket) {
            player.setEntityId(((ServerJoinGamePacket) msg).getEntityId());
            WorldUtil.dimSwitch(player, (ServerJoinGamePacket) msg);
            PacketUtil.onChangeServer(player);
        } else if (msg instanceof ServerLoginSuccessPacket) {
            this.remoteGameProfile.setUuid(((ServerLoginSuccessPacket) msg).getUuid());
            this.getPacketCodec().setConnectionState(EnumConnectionState.PLAY);
            this.netHandler.initHandler();
        } else if (msg instanceof ServerLoginSetCompressionPacket) {
            this.getPacketCodec().setCompressionThreshold(((ServerLoginSetCompressionPacket) msg).getThreshold());
        } else if (msg instanceof ServerLoginDisconnectPacket) {
            this.channel.attr(disconnectReason)
                    .set(((ServerLoginDisconnectPacket) msg).getMessage().getText());
            this.channel.close();
        } else if (msg instanceof ServerDisconnectPacket) {
            this.channel.attr(disconnectReason)
                    .set(((ServerDisconnectPacket) msg).getMessage().getText());
            this.channel.close();
        } else if (this.getPacketCodec().getConnectionState() == EnumConnectionState.LOGIN &&
                msg instanceof CustomPacket && ((CustomPacket) msg).getCustomPacketID() == 0x01) {
            this.channel.attr(disconnectReason)
                    .set("Лицензионные сервера не поддерживаются!");
            this.channel.close();
        } else if (msg instanceof ServerKeepAlivePacket) {
            this.sendPacket(new ClientKeepAlivePacket(((ServerKeepAlivePacket) msg).getKeepaliveId()));
        } else if (this.getNetHandler() != null) {
            if (player.getCurrentGui() != null
                    && (msg instanceof ServerOpenWindowPacket
                    || msg instanceof ServerCloseWindowPacket
                    || msg instanceof ServerSetSlotPacket
                    || msg instanceof ServerWindowItemsPacket))
                return;

            if (msg instanceof ServerPlayerListEntryPacket) {
                ServerPlayerListEntryPacket p = (ServerPlayerListEntryPacket) msg;

                for (PlayerListEntry entry : p.getEntries()) {
                    if (entry.getProfile().getUuid().equals(remoteGameProfile.getUuid())) {
                        entry.getProfile().setUuid(player.getGameProfile().getUuid());
                    }

                    if (p.getAction() == PlayerListEntryAction.ADD_PLAYER) {
                        player.getTabList().add(entry);
                    } else if (p.getAction() == PlayerListEntryAction.REMOVE_PLAYER) {
                        player.getTabList().remove(entry);
                    }
                }
            }

            this.getNetHandler().onRemoteServerPacket((Packet) msg);
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().attr(disconnectReason)
                .set("Internal exception: " + cause.getClass().getName() + ": " + cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }
}
