package ru.justnanix.bebraproxy.bots.network.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.Getter;
import lombok.Setter;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.network.packet.PacketRegistry;
import ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.CustomPacket;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.data.EnumConnectionState;
import ru.justnanix.bebraproxy.network.packet.PacketDirection;

import java.util.List;

@Getter
@Setter
public class PacketCodec extends ByteToMessageCodec<Packet> {
    private final PacketDirection packetDirection;
    private EnumConnectionState enumConnectionState;
    private int protocol;

    public PacketCodec(EnumConnectionState enumConnectionState, PacketDirection packetDirection) {
        this.enumConnectionState = enumConnectionState;
        this.packetDirection = packetDirection;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) {
        if (!byteBuf.isWritable()) return;

        PacketBuffer packetbuffer = new PacketBuffer(byteBuf);

        if (packet instanceof CustomPacket) {
            packetbuffer.writeVarInt(((CustomPacket) packet).getCustomPacketID());
        } else {
            packetbuffer.writeVarInt(getPacketIDByProtocol(packet, protocol));
        }

        try {
            packet.write(packetbuffer, protocol);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (!byteBuf.isReadable()) return;

        PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
        int packetID = packetBuffer.readVarInt();
        packetBuffer.bot = true;

        Packet packet = PacketRegistry.createPacket(enumConnectionState, packetDirection, packetID, protocol);

        try {
            packet.read(packetBuffer, protocol);
        } catch (Throwable ignored) {
        }

        list.add(packet);
        packetBuffer.clear();
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
}