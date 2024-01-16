package ru.justnanix.bebraproxy.network.packet;

import lombok.Data;
import ru.justnanix.bebraproxy.network.Protocol;
import ru.justnanix.bebraproxy.network.codec.PacketBuffer;
import ru.justnanix.bebraproxy.network.packet.impl.CustomPacket;
import ru.justnanix.bebraproxy.utils.network.PacketUtil;
import ru.justnanix.bebraproxy.utils.proxy.ReflectionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Packet {
    public abstract void write(PacketBuffer out, int protocol) throws Exception;

    public abstract void read(PacketBuffer in, int protocol) throws Exception;

    public abstract List<Protocol> getProtocolList();

    @Override
    public String toString() {
        return ReflectionUtil.objectToString(this);
    }

    @Data
    public static class Builder {
        private final List<CustomData> data = new ArrayList<>();

        public Builder init(CustomData... data) {
            this.data.addAll(Arrays.asList(data));

            return this;
        }

        public Builder add(DataType type, Object value) {
            data.add(new CustomData(type, value));

            return this;
        }

        public CustomPacket build(int id) {
            PacketBuffer buffer = PacketUtil.createEmptyPacketBuffer();

            for (CustomData customData : data) {
                switch (customData.getType()) {
                    case VARINT:
                        buffer.writeVarInt((Integer) customData.getValue());
                        break;
                    case INT:
                        buffer.writeInt((Integer) customData.getValue());
                        break;
                    case LONG:
                        buffer.writeLong((Long) customData.getValue());
                        break;
                    case DOUBLE:
                        buffer.writeDouble((Double) customData.getValue());
                        break;
                    case FLOAT:
                        buffer.writeFloat((Float) customData.getValue());
                        break;
                    case BYTE:
                        buffer.writeByte((Byte) customData.getValue());
                        break;
                    case SHORT:
                        buffer.writeShort((Short) customData.getValue());
                        break;
                    case BOOLEAN:
                        buffer.writeBoolean((Boolean) customData.getValue());
                        break;
                    case STRING:
                        buffer.writeString((String) customData.getValue());
                        break;
                    case BYTES:
                        buffer.writeBytes((byte[]) customData.getValue());
                        break;
                }
            }

            return new CustomPacket(id, buffer.readByteArray());
        }

        public enum DataType {
            VARINT, INT, LONG, DOUBLE, FLOAT, BYTE, SHORT, BOOLEAN, STRING, BYTES
        }

        @Data
        public static class CustomData {
            private final DataType type;
            private final Object value;
        }
    }
}