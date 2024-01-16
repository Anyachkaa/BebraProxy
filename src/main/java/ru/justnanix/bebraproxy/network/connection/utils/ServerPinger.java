package ru.justnanix.bebraproxy.network.connection.utils;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.Data;
import net.md_5.bungee.api.chat.BaseComponent;
import ru.justnanix.bebraproxy.network.ProxyServer;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.codec.PacketCodec;
import ru.justnanix.bebraproxy.network.codec.VarInt21FrameCodec;
import ru.justnanix.bebraproxy.network.data.EnumConnectionState;
import ru.justnanix.bebraproxy.network.data.status.ServerStatusInfo;
import ru.justnanix.bebraproxy.network.packet.PacketDirection;
import ru.justnanix.bebraproxy.network.packet.impl.client.HandshakePacket;
import ru.justnanix.bebraproxy.network.packet.impl.client.status.ClientStatusRequestPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.status.ServerStatusResponsePacket;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

@Data
public class ServerPinger {
    private final ProxiedPlayer owner;
    private final boolean showResult;

    public void connect(String host, int port, Proxy proxy) {
        Bootstrap bootstrap = new Bootstrap()
                .group(ProxyServer.nettyGroup)
                .channel(ProxyServer.nettyChannel)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.IP_TOS, 0x18)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        final ChannelPipeline pipeline = socketChannel.pipeline();
                        if (proxy != Proxy.NO_PROXY) {
                            pipeline.addFirst(new Socks4ProxyHandler(proxy.address()));
                        }
                        pipeline.addLast("timer", new ReadTimeoutHandler(30));
                        pipeline.addLast("frameCodec", new VarInt21FrameCodec());
                        pipeline.addLast("packetCodec", new PacketCodec(EnumConnectionState.STATUS, PacketDirection.CLIENTBOUND, socketChannel));
                        pipeline.addLast("handler", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                if (showResult)
                                    ChatUtil.sendChatMessage("&ePinging...", owner, true);
                                TimeUnit.MILLISECONDS.sleep(150);
                                ctx.writeAndFlush(new HandshakePacket(340, host, port, 1));
                                ctx.writeAndFlush(new ClientStatusRequestPacket());
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object packet) {
                                if (packet instanceof ServerStatusResponsePacket && showResult) {
                                    final ServerStatusInfo info = ((ServerStatusResponsePacket) packet).getInfo();
                                    ChatUtil.sendChatMessage("&7Max players: &c" + info.getPlayerInfo().getMaxPlayers(), owner, false);
                                    ChatUtil.sendChatMessage("&7Online players: &c" + info.getPlayerInfo().getOnlinePlayers(), owner, false);
                                    ChatUtil.sendChatMessage("&7MOTD: &c" + BaseComponent.toLegacyText(info.getDescription()), owner, false);
                                    ChatUtil.sendChatMessage("&7Version: &c" + info.getVersionInfo().getVersionName() +
                                            "(" + info.getVersionInfo().getProtocolVersion() + ")", owner, false);
                                }

                                ctx.channel().close();
                            }
                        });
                    }
                });
        bootstrap.connect(host, port);
    }
}