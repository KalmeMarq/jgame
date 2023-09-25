package me.kalmemarq.jgame.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BsoMap extends BsoTag {
    private final Map<String, BsoTag> entries;

    public BsoMap() {
        this.entries = new LinkedHashMap<>();
    }

    public BsoMap(Map<String, BsoTag> map) {
        this.entries = map;
    }

    @Override
    public BsoType<BsoMap> getType() {
        return BsoTypes.MAP;
    }

    @Override
    public void write(DataOutput output, boolean withAdditionalData) throws IOException {
        this.getType().write(output, this, withAdditionalData ? this.getAdditionalData() : 0);
    }

    @Override
    public byte getAdditionalData() {
        return BsoTag.getAdditionalDataFromIntegerRange(this.size());
    }

    public void put(String name, BsoTag tag) {
        this.entries.put(name, tag);
    }

    public void putByte(String name, byte value) {
        this.entries.put(name, new BsoByte(value));
    }

    public void putBoolean(String name, boolean value) {
        this.entries.put(name, new BsoBoolean(value));
    }

    public void putShort(String name, short value) {
        this.entries.put(name, new BsoShort(value));
    }

    public void putInt(String name, int value) {
        this.entries.put(name, new BsoInt(value));
    }

    public void putLong(String name, long value) {
        this.entries.put(name, new BsoLong(value));
    }

    public void putFloat(String name, float value) {
        this.entries.put(name, new BsoFloat(value));
    }

    public void putDouble(String name, double value) {
        this.entries.put(name, new BsoDouble(value));
    }

    public void putString(String name, String value) {
        this.entries.put(name, new BsoString(value));
    }

    public void putByteArray(String name, byte[] values) {
        this.entries.put(name, new BsoByteArray(values));
    }

    public void putShortArray(String name, short[] values) {
        this.entries.put(name, new BsoShortArray(values));
    }

    public void putIntArray(String name, int[] values) {
        this.entries.put(name, new BsoIntArray(values));
    }

    public boolean hasOfType(String key, BsoType<?> type) {
        BsoTag tag = this.entries.get(key);
        return tag != null && tag.getType() == type;
    }

    public BsoTag get(String key) {
        return this.entries.get(key);
    }

    public boolean getBoolean(String key) {
        return this.hasOfType(key, BsoTypes.BYTE) && ((BsoByte) this.entries.get(key)).booleanValue();
    }

    public byte getByte(String key) {
        return this.hasOfType(key, BsoTypes.BYTE) ? ((BsoByte) this.entries.get(key)).byteValue() : 0;
    }

    public short getShort(String key) {
        return this.hasOfType(key, BsoTypes.SHORT) ? ((BsoShort) this.entries.get(key)).shortValue() : 0;
    }

    public int getInt(String key) {
        return this.hasOfType(key, BsoTypes.INT) ? ((BsoInt) this.entries.get(key)).intValue() : 0;
    }

    public long getLong(String key) {
        return this.hasOfType(key, BsoTypes.LONG) ? ((BsoLong) this.entries.get(key)).longValue() : 0;
    }

    public float getFloat(String key) {
        return this.hasOfType(key, BsoTypes.FLOAT) ? ((BsoFloat) this.entries.get(key)).floatValue() : 0;
    }

    public double getDouble(String key) {
        return this.hasOfType(key, BsoTypes.DOUBLE) ? ((BsoDouble) this.entries.get(key)).doubleValue() : 0;
    }

    public String getString(String key) {
        return this.hasOfType(key, BsoTypes.STRING) ? ((BsoString) this.entries.get(key)).getValue() : "";
    }

    public BsoMap getMap(String key) {
        return this.hasOfType(key, BsoTypes.MAP) ? (BsoMap) this.entries.get(key) : new BsoMap();
    }

    public byte[] getByteArray(String key) {
        return this.hasOfType(key, BsoTypes.BYTE_ARRAY) ? ((BsoByteArray) this.entries.get(key)).getValues() : new byte[0];
    }

    public short[] getShortArray(String key) {
        return this.hasOfType(key, BsoTypes.SHORT_ARRAY) ? ((BsoShortArray) this.entries.get(key)).getValues() : new short[0];
    }

    public int[] getIntArray(String key) {
        return this.hasOfType(key, BsoTypes.INT_ARRAY) ? ((BsoIntArray) this.entries.get(key)).getValues() : new int[0];
    }

    public void remove(String key) {
        this.entries.remove(key);
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public void clear() {
        this.entries.clear();
    }

    public int size() {
        return this.entries.size();
    }

    public boolean containsKey(String key) {
        return this.entries.containsKey(key);
    }

    public boolean containsValue(BsoTag tag) {
        return this.entries.containsValue(tag);
    }

    public Set<String> keySet() {
        return this.entries.keySet();
    }

    public Set<Map.Entry<String, BsoTag>> entrySet() {
        return this.entries.entrySet();
    }

    @Override
    public BsoMap copy() {
        LinkedHashMap<String, BsoTag> map = new LinkedHashMap<>();
        for (Map.Entry<String, BsoTag> entry : this.entries.entrySet()) {
            map.put(entry.getKey(), entry.getValue().copy());
        }
        return new BsoMap(map);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return Objects.equals(this.entries, ((BsoMap) obj).entries);
    }

    @Override
    public int hashCode() {
        return this.entries.hashCode();
    }
}
