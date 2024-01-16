package ru.justnanix.bebraproxy.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.Data;
import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.ConnectionManagerProxy;
import ru.justnanix.bebraproxy.network.connection.ConnectionManagerRemote;
import ru.justnanix.bebraproxy.network.connection.ServerInfo;
import ru.justnanix.bebraproxy.player.plan.PlanAccount;
import ru.justnanix.bebraproxy.network.codec.PacketCodec;
import ru.justnanix.bebraproxy.network.codec.VarInt21FrameCodec;
import ru.justnanix.bebraproxy.network.data.EnumConnectionState;
import ru.justnanix.bebraproxy.network.data.GameProfile;
import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.network.packet.PacketDirection;
import ru.justnanix.bebraproxy.network.packet.impl.client.HandshakePacket;
import ru.justnanix.bebraproxy.network.packet.impl.client.login.ClientLoginStartPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.login.ServerLoginDisconnectPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerDisconnectPacket;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ProxyServer {
    public static final EventLoopGroup nettyGroup = Epoll.isAvailable() ? new EpollEventLoopGroup(4) : new NioEventLoopGroup(4);
    public static final Class<? extends SocketChannel> nettyChannel = Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class;

    public static final String UNKNOWN_VERSION =
            "§c§lBebra§f§lProxy§r\n\n" +
                    "§7Вы зашли с неподдерживаемой версии!\n" +
                    "§7Поддерживаемые версии: §e1.8.x-1.12.x";
    public static final String BLOCKED_IP =
            "§c§lBebra§f§lProxy§r\n\n" +
                    "§cВаш айпи заблокирован.";
    public static final String NO_ACCESS =
            "§c§lBebra§f§lProxy§r\n\n" +
                    "§7Вы не имеете доступа!\n" +
                    "§7Для того чтобы получить доступ купите тариф.\n\n" +
                    "§7Купить тариф - §edsc.gg/bebraproxy";
    public static final String ALREADY_PLAYING =
            "§c§lBebra§f§lProxy§r\n\n" +
                    "§7Игрок с таким ником уже играет на сервере!";

    private final Set<ProxiedPlayer> players = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final String icon;

    public void bind() {
        new ServerBootstrap()
                .group(nettyGroup)
                .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();

                        pipeline.addLast("timer", new ReadTimeoutHandler(30));
                        pipeline.addLast("frameCodec", new VarInt21FrameCodec());
                        pipeline.addLast("packetCodec", new PacketCodec(EnumConnectionState.HANDSHAKE, PacketDirection.CLIENTBOUND, socketChannel));
                        pipeline.addLast("initialHandler", new SimpleChannelInboundHandler<Packet>() {
                            int protocolID = -1;

                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, Packet p) {
                                System.out.println(ctx.channel().localAddress());
                                System.out.println(p.toString());
                                if (p instanceof HandshakePacket) {
                                    HandshakePacket handshake = (HandshakePacket) p;
                                    if (handshake.getNextState() == 2) {
                                        if (handshake.getProtocolId() > 340 || handshake.getProtocolId() < 47) {
                                            ctx.channel().writeAndFlush(new ServerLoginDisconnectPacket(UNKNOWN_VERSION))
                                                    .addListener(ChannelFutureListener.CLOSE);
                                        }

                                        this.protocolID = handshake.getProtocolId();
                                        ((PacketCodec) ctx.channel().pipeline().get("packetCodec")).setProtocol(protocolID);
                                        ((PacketCodec) ctx.channel().pipeline().get("packetCodec")).setConnectionState(EnumConnectionState.LOGIN);
                                    } else {
                                        ctx.channel().close();
                                    }
                                } else if (p instanceof ClientLoginStartPacket) {
                                    ClientLoginStartPacket packet = (ClientLoginStartPacket) p;
                                    PlanAccount account = BebraProxy.getInstance().getPlanManager().getAccountByKeyName(packet.getUsername());

                                    if (account == null) {
                                        ctx.channel().writeAndFlush(new ServerLoginDisconnectPacket(NO_ACCESS))
                                                .addListener(ChannelFutureListener.CLOSE);
                                        return;
                                    }

                                    if (players.stream().anyMatch(player -> player.getAccount().getKeyName()
                                            .equalsIgnoreCase(packet.getUsername()))) {
                                        ctx.channel().writeAndFlush(new ServerLoginDisconnectPacket(ALREADY_PLAYING))
                                                .addListener(ChannelFutureListener.CLOSE);
                                        return;
                                    }

                                    createPlayer(ctx.channel(), account);
                                }
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                cause.printStackTrace();
                                ctx.close();
                            }
                        });
                    }
                }).bind(25565);
    }

    public void connectPlayerToServer(ProxiedPlayer player, String host, int port) {
        new ConnectionManagerRemote(player.getConnectMgr().getChannel(), player).connectToRemote(player.getGameProfile().getName(), new ServerInfo(host, port));
    }

    public void connectPlayerToLobby(ProxiedPlayer player) {
        new ConnectionManagerRemote(player.getConnectMgr().getChannel(), player).connectToLobby();
    }

    public void createPlayer(Channel channel, PlanAccount account) {
        ProxiedPlayer player = new ProxiedPlayer(new GameProfile(UUID.randomUUID(), account.getKeyName()), account);
        ConnectionManagerProxy connectionManager = new ConnectionManagerProxy(channel, player);
        channel.pipeline().replace("initialHandler", "playerHandler", connectionManager);
        player.setConnectMgr(connectionManager);
        connectionManager.initialConnect();
        players.add(player);
    }

    public void disconnectPlayer(ProxiedPlayer player, String reason) {
        player.getConnectMgr().sendPacket(new ServerDisconnectPacket(reason)).addListener(ChannelFutureListener.CLOSE);
        this.disconnectPlayer(player);
    }

    public void disconnectPlayer(ProxiedPlayer player) {
        if (players.contains(player)) {
            players.remove(player);
            player.getConnectMgr().getChannel().close();
            if (player.getRemoteConnectMgr() != null && player.getRemoteConnectMgr().isChannelOpen())
                player.getRemoteConnectMgr().getChannel().close();
            player.getOptionsManager().saveOptions();
        }
    }

    public Optional<ProxiedPlayer> getPlayer(String name) {
        return players.stream().filter(p -> p.getAccount().getKeyName().equalsIgnoreCase(name)).findFirst();
    }
}