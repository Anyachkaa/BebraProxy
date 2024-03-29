package net.minecraft.nbt;

import com.google.common.collect.Maps;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class NBTTagCompound extends NBTBase {
    private static final Logger logger = LogManager.getLogManager().getLogger("Minecraft");
    private static final String __OBFID = "CL_00001215";
    private final Map tagMap = Maps.newHashMap();

    private static void writeEntry(String name, NBTBase data, DataOutput output) throws IOException {
        output.writeByte(data.getId());

        if (data.getId() != 0) {
            output.writeUTF(name);
            data.write(output);
        }
    }

    private static byte readType(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
        return input.readByte();
    }

    private static String readKey(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
        return input.readUTF();
    }

    static NBTBase readNBT(byte id, String key, DataInput input, int depth, NBTSizeTracker sizeTracker) {
        NBTBase var5 = createNewByType(id);

        try {
            var5.read(input, depth, sizeTracker);
            return var5;
        } catch (IOException var9) {
            throw new RuntimeException(var9);
        }
    }

    void write(DataOutput output) throws IOException {
        Iterator var2 = this.tagMap.keySet().iterator();

        while (var2.hasNext()) {
            String var3 = (String) var2.next();
            NBTBase var4 = (NBTBase) this.tagMap.get(var3);
            writeEntry(var3, var4, output);
        }

        output.writeByte(0);
    }

    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
        if (depth > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        } else {
            this.tagMap.clear();
            byte var4;

            while ((var4 = readType(input, sizeTracker)) != 0) {
                String var5 = readKey(input, sizeTracker);
                sizeTracker.read(16 * var5.length());
                NBTBase var6 = readNBT(var4, var5, input, depth + 1, sizeTracker);
                this.tagMap.put(var5, var6);
            }
        }
    }

    public Set getKeySet() {
        return this.tagMap.keySet();
    }

    public byte getId() {
        return (byte) 10;
    }

    public void setTag(String key, NBTBase value) {
        this.tagMap.put(key, value);
    }

    public void setByte(String key, byte value) {
        this.tagMap.put(key, new NBTTagByte(value));
    }

    public void setShort(String key, short value) {
        this.tagMap.put(key, new NBTTagShort(value));
    }

    public void setInteger(String key, int value) {
        this.tagMap.put(key, new NBTTagInt(value));
    }

    public void setLong(String key, long value) {
        this.tagMap.put(key, new NBTTagLong(value));
    }

    public void setFloat(String key, float value) {
        this.tagMap.put(key, new NBTTagFloat(value));
    }

    public void setDouble(String key, double value) {
        this.tagMap.put(key, new NBTTagDouble(value));
    }

    public void setString(String key, String value) {
        this.tagMap.put(key, new NBTTagString(value));
    }

    public void setByteArray(String key, byte[] value) {
        this.tagMap.put(key, new NBTTagByteArray(value));
    }

    public void setIntArray(String key, int[] value) {
        this.tagMap.put(key, new NBTTagIntArray(value));
    }

    public void setBoolean(String key, boolean value) {
        this.setByte(key, (byte) (value ? 1 : 0));
    }

    public NBTBase getTag(String key) {
        return (NBTBase) this.tagMap.get(key);
    }

    public byte getTagType(String key) {
        NBTBase var2 = (NBTBase) this.tagMap.get(key);
        return var2 != null ? var2.getId() : 0;
    }

    public boolean hasKey(String key) {
        return this.tagMap.containsKey(key);
    }

    public boolean hasKey(String key, int type) {
        byte var3 = this.getTagType(key);

        if (var3 == type) {
            return true;
        } else if (type != 99) {

            return false;
        } else {
            return var3 == 1 || var3 == 2 || var3 == 3 || var3 == 4 || var3 == 5 || var3 == 6;
        }
    }

    public byte getByte(String key) {
        try {
            return !this.hasKey(key, 99) ? 0 : ((NBTPrimitive) this.tagMap.get(key)).getByte();
        } catch (ClassCastException var3) {
            return (byte) 0;
        }
    }

    public short getShort(String key) {
        try {
            return !this.hasKey(key, 99) ? 0 : ((NBTPrimitive) this.tagMap.get(key)).getShort();
        } catch (ClassCastException var3) {
            return (short) 0;
        }
    }

    public int getInteger(String key) {
        try {
            return !this.hasKey(key, 99) ? 0 : ((NBTPrimitive) this.tagMap.get(key)).getInt();
        } catch (ClassCastException var3) {
            return 0;
        }
    }

    public long getLong(String key) {
        try {
            return !this.hasKey(key, 99) ? 0L : ((NBTPrimitive) this.tagMap.get(key)).getLong();
        } catch (ClassCastException var3) {
            return 0L;
        }
    }

    public float getFloat(String key) {
        try {
            return !this.hasKey(key, 99) ? 0.0F : ((NBTPrimitive) this.tagMap.get(key)).getFloat();
        } catch (ClassCastException var3) {
            return 0.0F;
        }
    }

    public double getDouble(String key) {
        try {
            return !this.hasKey(key, 99) ? 0.0D : ((NBTPrimitive) this.tagMap.get(key)).getDouble();
        } catch (ClassCastException var3) {
            return 0.0D;
        }
    }

    public String getString(String key) {
        try {
            return !this.hasKey(key, 8) ? "" : ((NBTBase) this.tagMap.get(key)).getString();
        } catch (ClassCastException var3) {
            return "";
        }
    }

    public byte[] getByteArray(String key) {
        try {
            return !this.hasKey(key, 7) ? new byte[0] : ((NBTTagByteArray) this.tagMap.get(key)).getByteArray();
        } catch (ClassCastException var3) {
            throw new RuntimeException(var3);
        }
    }

    public int[] getIntArray(String key) {
        try {
            return !this.hasKey(key, 11) ? new int[0] : ((NBTTagIntArray) this.tagMap.get(key)).getIntArray();
        } catch (ClassCastException var3) {
            throw new RuntimeException(var3);
        }
    }

    public NBTTagCompound getCompoundTag(String key) {
        try {
            return !this.hasKey(key, 10) ? new NBTTagCompound() : (NBTTagCompound) this.tagMap.get(key);
        } catch (ClassCastException var3) {
            throw new RuntimeException(var3);
        }
    }

    public NBTTagList getTagList(String key, int type) {
        try {
            if (this.getTagType(key) != 9) {
                return new NBTTagList();
            } else {
                NBTTagList var3 = (NBTTagList) this.tagMap.get(key);
                return var3.tagCount() > 0 && var3.getTagType() != type ? new NBTTagList() : var3;
            }
        } catch (ClassCastException var4) {
            throw new RuntimeException(var4);
        }
    }

    public boolean getBoolean(String key) {
        return this.getByte(key) != 0;
    }

    public void removeTag(String key) {
        this.tagMap.remove(key);
    }

    public String toString() {
        String var1 = "{";
        String var3;

        for (Iterator var2 = this.tagMap.keySet().iterator(); var2.hasNext(); var1 = var1 + var3 + ':' + this.tagMap.get(var3) + ',') {
            var3 = (String) var2.next();
        }

        return var1 + "}";
    }

    public boolean hasNoTags() {
        return this.tagMap.isEmpty();
    }

    public NBTBase copy() {
        NBTTagCompound var1 = new NBTTagCompound();
        Iterator var2 = this.tagMap.keySet().iterator();

        while (var2.hasNext()) {
            String var3 = (String) var2.next();
            var1.setTag(var3, ((NBTBase) this.tagMap.get(var3)).copy());
        }

        return var1;
    }

    public boolean equals(Object p_equals_1_) {
        if (super.equals(p_equals_1_)) {
            NBTTagCompound var2 = (NBTTagCompound) p_equals_1_;
            return this.tagMap.entrySet().equals(var2.tagMap.entrySet());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return super.hashCode() ^ this.tagMap.hashCode();
    }

    public void merge(NBTTagCompound other) {
        Iterator var2 = other.tagMap.keySet().iterator();

        while (var2.hasNext()) {
            String var3 = (String) var2.next();
            NBTBase var4 = (NBTBase) other.tagMap.get(var3);

            if (var4.getId() == 10) {
                if (this.hasKey(var3, 10)) {
                    NBTTagCompound var5 = this.getCompoundTag(var3);
                    var5.merge((NBTTagCompound) var4);
                } else {
                    this.setTag(var3, var4.copy());
                }
            } else {
                this.setTag(var3, var4.copy());
            }
        }
    }
}
