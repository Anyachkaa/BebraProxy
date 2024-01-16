package ru.justnanix.bebraproxy.network.packet.impl.server.play;

import com.github.steveice10.mc.protocol.data.MagicValues;
import com.github.steveice10.mc.protocol.data.game.MessageType;
import com.github.steveice10.mc.protocol.data.message.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.data.MessagePosition;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
public class ServerChatPacket extends Packet {
    private BaseComponent[] message;
    private MessagePosition position;

    private Message msg;
    private MessageType type;

    public ServerChatPacket(BaseComponent[] message, MessagePosition position) {
        this.message = message;
        this.position = position;
    }

    public ServerChatPacket(String message) {
        this(new ComponentBuilder(message).create(), MessagePosition.CHATBOX);
    }

    public ServerChatPacket(String message, MessagePosition position) {
        this(new ComponentBuilder(message).create(), position);
    }

    public ServerChatPacket(BaseComponent... text) {
        this(text, MessagePosition.CHATBOX);
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeString(ComponentSerializer.toString(message));
        out.writeByte(position.getId());
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        String str = in.readString();
        byte byt = in.readByte();

        this.message = ComponentSerializer.parse(str);
        this.position = MessagePosition.getById(byt);

        this.msg = Message.fromString(str);
        this.type = (MessageType) MagicValues.key(MessageType.class, byt);
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(
                new Protocol(0x02, 47),
                new Protocol(0x0F, 107, 108, 109, 110, 210, 315, 316, 335, 338, 340)
        );
    }
}