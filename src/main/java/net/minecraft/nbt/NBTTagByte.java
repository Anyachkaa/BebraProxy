package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTBase.NBTPrimitive {
    private static final String __OBFID = "CL_00001214";
    private byte data;

    NBTTagByte() {
    }

    public NBTTagByte(byte data) {
        this.data = data;
    }

    void write(DataOutput output) throws IOException {
        output.writeByte(this.data);
    }

    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        sizeTracker.read(8L);
        this.data = input.readByte();
    }

    public byte getId() {
        return (byte) 1;
    }

    public String toString() {
        return "" + this.data + "b";
    }

    public NBTBase copy() {
        return new NBTTagByte(this.data);
    }

    public boolean equals(Object p_equals_1_) {
        if (super.equals(p_equals_1_)) {
            NBTTagByte var2 = (NBTTagByte) p_equals_1_;
            return this.data == var2.data;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return super.hashCode() ^ this.data;
    }

    public long getLong() {
        return this.data;
    }

    public int getInt() {
        return this.data;
    }

    public short getShort() {
        return this.data;
    }

    public byte getByte() {
        return this.data;
    }

    public double getDouble() {
        return this.data;
    }

    public float getFloat() {
        return this.data;
    }
}
