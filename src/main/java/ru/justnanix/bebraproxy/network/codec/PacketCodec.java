package ru.justnanix.bebraproxy.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.data.EnumConnectionState;
import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.network.packet.PacketDirection;
import ru.justnanix.bebraproxy.network.packet.PacketRegistry;
import ru.justnanix.bebraproxy.network.packet.impl.CustomPacket;

import java.util.List;

@RequiredArgsConstructor
@Getter @Setter
public class PacketCodec extends ByteToMessageCodec<Packet> {
    @NonNull private EnumConnectionState connectionState;
    private final PacketDirection packetDirection;
    private final Channel channel;

    private int protocol;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) {
        PacketBuffer packetbuffer = new PacketBuffer(byteBuf);
        if (packet instanceof CustomPacket)
            packetbuffer.writeVarInt(((CustomPacket) packet).getCustomPacketID());
        else packetbuffer.writeVarInt(getPacketIDByProtocol(packet, protocol));

        try {
            packet.write(packetbuffer, protocol);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        PacketBuffer packetBuffer = new PacketBuffer(byteBuf);

        int packetID = packetBuffer.readVarInt();
        Packet packet = PacketRegistry.createPacket(connectionState, packetDirection, packetID, protocol);
        packet.read(packetBuffer, protocol);
        list.add(packet);
    }

    private int getPacketIDByProtocol(Packet packet, int protocol) {
        for (Protocol p : packet.getProtocolList()) {
            for (int protocol2 : p.getProtocols()) {
                if (protocol2 == protocol) {
                    return p.getId();
                }
            }
        }

        return packet.getProtocolList().get(packet.getProtocolList().size() - 1).getId();
    }

    public void setCompressionThreshold(final int threshold) {
        if (connectionState == EnumConnectionState.LOGIN) {
            if (channel.pipeline().get("compression") == null) {
                channel.pipeline().addBefore("packetCodec", "compression", new CompressionCodec(threshold));
            } else {
                ((CompressionCodec) channel.pipeline().get("compression")).setCompressionThreshold(threshold);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }
}