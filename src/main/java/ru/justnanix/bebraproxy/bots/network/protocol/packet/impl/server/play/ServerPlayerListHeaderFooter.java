package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServerPlayerListHeaderFooter extends Packet {
    private BaseComponent[] header, footer;

    public ServerPlayerListHeaderFooter(String header, String footer) {
        this(new ComponentBuilder(header).create(), new ComponentBuilder(footer).create());
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(ComponentSerializer.toString(this.header));
        out.writeString(ComponentSerializer.toString(this.footer));
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.header = ComponentSerializer.parse(in.readString());
        this.footer = ComponentSerializer.parse(in.readString());
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x48, 107, 108, 109), new Protocol(0x47, 47, 110, 210, 315, 316), new Protocol(0x4A, 338, 340), new Protocol(0x49, 335));
    }
}