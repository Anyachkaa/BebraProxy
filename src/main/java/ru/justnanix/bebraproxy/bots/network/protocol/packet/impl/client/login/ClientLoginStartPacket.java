package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.client.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.ProtocolType;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientLoginStartPacket extends Packet {
    private String username;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(this.username);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.username = in.readString(128);
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Collections.singletonList(new Protocol(0x00, ProtocolType.values()));
    }
}