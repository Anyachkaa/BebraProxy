package ru.justnanix.bebraproxy.network.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.handler.NetHandler;
import ru.justnanix.bebraproxy.network.connection.handler.proxy.NetHandlerAuthProxy;
import ru.justnanix.bebraproxy.network.codec.PacketCodec;
import ru.justnanix.bebraproxy.network.data.EnumConnectionState;
import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.network.packet.impl.client.play.ClientKeepAlivePacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.login.ServerLoginSetCompressionPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.login.ServerLoginSuccessPacket;
import ru.justnanix.bebraproxy.network.packet.impl.server.play.ServerKeepAlivePacket;

import java.util.concurrent.TimeUnit;

@Getter @Setter
public class ConnectionManagerProxy extends ConnectionManager {
    private final ProxiedPlayer player;
    private NetHandler netHandler;

    public ConnectionManagerProxy(Channel channel, ProxiedPlayer player) {
        super(channel);
        this.player = player;
    }

    public void initialConnect() {
        this.sendPacket(new ServerLoginSetCompressionPacket(256));
        this.getPacketCodec().setCompressionThreshold(256);
        this.sendPacket(new ServerLoginSuccessPacket(player.getGameProfile().getUuid(), player.getAccount().getKeyName()));
        this.getPacketCodec().setConnectionState(EnumConnectionState.PLAY);
        this.channel.eventLoop().scheduleAtFixedRate(() -> channel.writeAndFlush(new ServerKeepAlivePacket(System.currentTimeMillis())),
                3L, 3L, TimeUnit.SECONDS);
        this.setNetHandler(new NetHandlerAuthProxy(player));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (netHandler != null && !(msg instanceof ClientKeepAlivePacket)) {
            netHandler.onClientPacket((Packet) msg);
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[" + player.getAccount().getKeyName() + "] Отключился");
        BebraProxy.getInstance().getServer().disconnectPlayer(player);
        super.channelInactive(ctx);
    }
}
