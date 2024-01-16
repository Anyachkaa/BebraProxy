package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.client.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.data.Face;
import ru.justnanix.bebraproxy.network.data.ItemStack;
import ru.justnanix.bebraproxy.network.data.Position;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientPlayerPlaceBlockPacket extends Packet {
    private Position position;
    private Face face;
    private ItemStack held;
    private float cursorX;
    private float cursorY;
    private float cursorZ;
    private int hand;

    public ClientPlayerPlaceBlockPacket(Position position, Face face, ItemStack held, float cursorX, float cursorY, float cursorZ) {
        this.position = position;
        this.face = face;
        this.held = held;
        this.cursorX = cursorX;
        this.cursorY = cursorY;
        this.cursorZ = cursorZ;
    }

    public ClientPlayerPlaceBlockPacket(Position position, Face face, int hand, float cursorX, float cursorY, float cursorZ) {
        this.position = position;
        this.face = face;
        this.hand = hand;
        this.cursorX = cursorX;
        this.cursorY = cursorY;
        this.cursorZ = cursorZ;
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writePosition(position);
        if (protocol >= 107) {
            out.writeVarInt(this.face.getId());
            out.writeVarInt(this.hand);
            if (protocol == 110 || protocol == 107 || protocol == 108) {
                out.writeByte((int) this.cursorX);
                out.writeByte((int) this.cursorY);
                out.writeByte((int) this.cursorZ);
            } else {
                out.writeFloat(this.cursorX);
                out.writeFloat(this.cursorY);
                out.writeFloat(this.cursorZ);
            }
        } else {
            out.writeByte(this.face.getId());
            out.writeItemStack(held);
            out.writeByte((int) (this.cursorX * 16.0F));
            out.writeByte((int) (this.cursorY * 16.0F));
            out.writeByte((int) (this.cursorZ * 16.0F));
        }
    }

    @Override
    public void read(PacketBuffer in, int protocol) throws Exception {
        this.position = in.readPosition();
        if (protocol >= 107) {
            this.face = Face.getById(in.readVarInt());
            this.hand = in.readVarInt();

            if (protocol == 110 || protocol == 107 || protocol == 108) {
                this.cursorX = in.readByte();
                this.cursorY = in.readByte();
                this.cursorZ = in.readByte();
            } else {
                this.cursorX = in.readFloat();
                this.cursorY = in.readFloat();
                this.cursorZ = in.readFloat();
            }
        } else {
            this.face = Face.getById(in.readUnsignedByte());
            this.held = in.readItemStack();
            this.cursorX = (float) in.readByte() / 16.0F;
            this.cursorY = (float) in.readByte() / 16.0F;
            this.cursorZ = (float) in.readByte() / 16.0F;
        }
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x08, 47), new Protocol(0x1C, 107, 108, 110, 315, 316), new Protocol(0x1F, 335, 338, 340));
    }
}