package ru.justnanix.bebraproxy.network.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientAnimationPacket extends Packet {
    private EnumHand hand;

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeEnumValue(this.hand);
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.hand = (EnumHand)in.readEnumValue(EnumHand.class);
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x1D, 340));
    }

    public enum EnumHand {
        MAIN_HAND,
        OFF_HAND;
    }
}
