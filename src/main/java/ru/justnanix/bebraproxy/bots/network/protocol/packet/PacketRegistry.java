package ru.justnanix.bebraproxy.bots.network.protocol.packet;

import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.CustomPacket;
import ru.justnanix.bebraproxy.bots.network.protocol.packet.impl.client.HandshakePacket;
import ru.justnanix.bebraproxy.network.data.EnumConnectionState;
import ru.justnanix.bebraproxy.network.packet.Packet;
import ru.justnanix.bebraproxy.network.packet.PacketDirection;
import ru.justnanix.bebraproxy.utils.proxy.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class PacketRegistry {
    static {
        Arrays.asList(PacketDirection.values())
                .forEach(direction -> Arrays.stream(EnumConnectionState.values())
                        .filter(connectionState -> connectionState != EnumConnectionState.HANDSHAKE)
                        .forEach(state -> ReflectionUtil.getClasses("ru.justnanix.bebraproxy.bots.network.network.packet.impl." + (direction == PacketDirection.SERVERBOUND ? "remote" : "server") + "." + state.name().toLowerCase(), Packet.class)
                                .forEach(packet -> {
                                            try {
                                                if (!Modifier.isPublic(packet.getClass().getModifiers())) {
                                                    throw new IllegalAccessException("Packet " + packet.getClass().getSimpleName() + " has a non public default constructor.");
                                                }

                                                state.getPacketsByDirection(direction).add(packet);
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                )
                        )
                );
    }

    public static Packet createPacket(EnumConnectionState enumConnectionState, PacketDirection direction, int id, int protocol) {
        Packet packetIn = getPacket(enumConnectionState, direction, id, protocol);

        if (packetIn == null) return new CustomPacket(id);

        try {
            Constructor<? extends Packet> constructor = packetIn.getClass().getDeclaredConstructor();

            constructor.setAccessible(true);

            return constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate packet \"" + packetIn.getClass().getName() + "\".", e);
        }
    }

    private static Packet getPacket(EnumConnectionState enumConnectionState, PacketDirection direction, int id, int protocol) {
        if (enumConnectionState == EnumConnectionState.HANDSHAKE) {
            return new HandshakePacket();
        }

        for (Packet packet : enumConnectionState.getPacketsByDirection(direction)) {
            for (Protocol protocol2 : packet.getProtocolList()) {
                if (protocol2.getId() == id) {
                    for (int p : protocol2.getProtocols()) {
                        if (p == protocol) {
                            return packet;
                        }
                    }
                }
            }
        }

        return null;
    }
}