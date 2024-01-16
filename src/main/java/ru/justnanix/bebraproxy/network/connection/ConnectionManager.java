package ru.justnanix.bebraproxy.network.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.justnanix.bebraproxy.network.codec.PacketCodec;
import ru.justnanix.bebraproxy.network.packet.Packet;

@RequiredArgsConstructor
@Getter
public abstract class ConnectionManager extends ChannelInboundHandlerAdapter {
    @NonNull
    protected Channel channel;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();

        super.exceptionCaught(ctx, cause);
    }

    public ChannelFuture sendPacket(Packet p) {
        return this.channel.writeAndFlush(p);
    }

    public PacketCodec getPacketCodec() {
        return (PacketCodec) channel.pipeline().get("packetCodec");
    }

    public boolean isChannelOpen() {
        return channel.isOpen();
    }
}
