package ru.justnanix.bebraproxy.bots.network.protocol.data;

import io.netty.channel.Channel;
import lombok.Data;
import ru.justnanix.bebraproxy.bots.network.handler.NetHandler;
import ru.justnanix.bebraproxy.network.ProtocolType;
import ru.justnanix.bebraproxy.bots.network.protocol.codec.PacketCodec;
import ru.justnanix.bebraproxy.network.data.EnumConnectionState;
import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.network.codec.CompressionCodec;

@Data
public class Session {
    private final Channel channel;
    private NetHandler packetHandler;
    private String username;

    public boolean noMessage = false;

    public void sendPackets(Packet... packets) {
        if (this.isChannelOpen()) {
            for (Packet p : packets) {
                this.channel.write(p);
            }

            this.channel.flush();
        }
    }

    public void sendPacket(Packet p) {
        if (this.isChannelOpen()) {
            this.channel.writeAndFlush(p);
        }
    }

    public void closeChannelSilent() {
        this.noMessage = true;
        this.channel.close();
    }

    public boolean isChannelOpen() {
        return this.channel != null && this.channel.isOpen();
    }

    public int getProtocolID() {
        if (getPacketCodec() == null) {
            return ProtocolType.PROTOCOL_UNKNOWN.getProtocol();
        }

        return getPacketCodec().getProtocol();
    }

    public void setProtocolID(int protocol) {
        getPacketCodec().setProtocol(protocol);
    }

    public EnumConnectionState getConnectionState() {
        return getPacketCodec().getEnumConnectionState();
    }

    public void setConnectionState(EnumConnectionState state) {
        getPacketCodec().setEnumConnectionState(state);
    }

    public PacketCodec getPacketCodec() {
        return ((PacketCodec) channel.pipeline().get("packetCodec"));
    }

    public void setCompressionThreshold(final int threshold) {
        if (getConnectionState() == EnumConnectionState.LOGIN) {
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