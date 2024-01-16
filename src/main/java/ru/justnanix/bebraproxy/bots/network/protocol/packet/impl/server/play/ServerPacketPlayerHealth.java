package ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.server.play;

import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.Packet;

import java.util.Arrays;
import java.util.List;

public class ServerPacketPlayerHealth extends Packet {
    private float health, foodSat;
    private int food;

    public ServerPacketPlayerHealth() {
    }

    @Override
    public void read(PacketBuffer in, int protocol) {
        health = in.readFloat();
        food = in.readVarInt();
        foodSat = in.readFloat();
    }

    @Override
    public void write(PacketBuffer out, int protocol) throws Exception {
        out.writeFloat(health);
        out.writeVarInt(food);
        out.writeFloat(foodSat);
    }

    @Override
    public List<Protocol> getProtocolList() {
        return Arrays.asList(new Protocol(0x41, 340));
    }

    public float getHealth() {
        return health;
    }

    public float getFoodSat() {
        return foodSat;
    }

    public int getFood() {
        return food;
    }
}
