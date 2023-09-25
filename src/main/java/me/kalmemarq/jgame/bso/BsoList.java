package me.kalmemarq.jgame.bso;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class BsoList extends BsoTag implements Iterable<BsoTag> {
    private final List<BsoTag> list;
    private BsoType<?> singleType = null;

    public BsoList() {
        this(new ArrayList<>());
    }

    public BsoList(List<BsoTag> list) {
        this.list = list;
    }

    public BsoList(List<BsoTag> list, BsoType<?> singleType) {
        this.list = list;
        this.singleType = singleType;
    }

    public BsoType<?> getSingleType() {
        return this.singleType;
    }

    @Override
    public BsoType<BsoList> getType() {
        return BsoTypes.LIST;
    }

    @Override
    public void write(DataOutput output, boolean withAdditionalData) throws IOException {
        this.getType().write(output, this, withAdditionalData ? this.getAdditionalData() : 0);
    }

    @Override
    public byte getAdditionalData() {
        return (byte) (BsoTag.getAdditionalDataFromIntegerRange(this.size()) + (this.singleType != null ? 4 : 0));
    }

    @NotNull
    @Override
    public Iterator<BsoTag> iterator() {
        return this.list.iterator();
    }

    public BsoTag remove(int index) {
        return this.list.remove(index);
    }

    public void remove(BsoTag obj) {
        this.list.remove(obj);
    }

    public BsoTag get(int index) {
        return this.list.get(index);
    }

    public boolean add(BsoTag tag) {
        if (this.singleType != null && tag.getType() != this.singleType) {
            throw new UnsupportedOperationException("Trying to add tag of type " + tag.getType().getId() + " to list of " + this.singleType.getId());
        }
        return this.list.add(tag);
    }

    public void add(int index, BsoTag tag) {
        if (this.singleType != null && tag.getType() != this.singleType) {
            throw new UnsupportedOperationException("Trying to add tag of type " + tag.getType().getId() + " to list of " + this.singleType.getId());
        }
        this.list.add(index, tag);
    }

    public boolean isOfType(int index, BsoType<?> type) {
        return this.list.get(index).getType() == type;
    }

    public byte getByte(int index) {
        BsoTag tag;
        if (index >= 0 && index < this.size() && (tag = this.get(index)).getType() == BsoTypes.BYTE) {
            return ((BsoByte) tag).byteValue();
        }
        return 0;
    }

    public boolean getBoolean(int index) {
        return this.getByte(index) != 0;
    }

    public short getShort(int index) {
        BsoTag tag;
        if (index >= 0 && index < this.size() && (tag = this.get(index)).getType() == BsoTypes.SHORT) {
            return ((BsoShort) tag).shortValue();
        }
        return 0;
    }

    public int getInt(int index) {
        BsoTag tag;
        if (index >= 0 && index < this.size() && (tag = this.get(index)).getType() == BsoTypes.INT) {
            return ((BsoInt) tag).intValue();
        }
        return 0;
    }

    public long getLong(int index) {
        BsoTag tag;
        if (index >= 0 && index < this.size() && (tag = this.get(index)).getType() == BsoTypes.LONG) {
            return ((BsoLong) tag).longValue();
        }
        return 0;
    }

    public float getFloat(int index) {
        BsoTag tag;
        if (index >= 0 && index < this.size() && (tag = this.get(index)).getType() == BsoTypes.FLOAT) {
            return ((BsoFloat) tag).floatValue();
        }
        return 0;
    }

    public double getDouble(int index) {
        BsoTag tag;
        if (index >= 0 && index < this.size() && (tag = this.get(index)).getType() == BsoTypes.DOUBLE) {
            return ((BsoDouble) tag).doubleValue();
        }
        return 0;
    }

    public String getString(int index) {
        BsoTag tag;
        if (index >= 0 && index < this.size() && (tag = this.get(index)).getType() == BsoTypes.STRING) {
            return ((BsoString) tag).getValue();
        }
        return "";
    }

    public BsoMap getMap(int index) {
        BsoTag tag;
        if (index >= 0 && index < this.size() && (tag = this.get(index)).getType() == BsoTypes.MAP) {
            return (BsoMap) tag;
        }
        return new BsoMap();
    }

    public BsoList getList(int index) {
        BsoTag tag;
        if (index >= 0 && index < this.size() && (tag = this.get(index)).getType() == BsoTypes.LIST) {
            return (BsoList) tag;
        }
        return new BsoList();
    }

    public byte[] getByteArray(int index) {
        BsoTag tag;
        if (index >= 0 && index < this.size() && (tag = this.get(index)).getType() == BsoTypes.BYTE_ARRAY) {
            return ((BsoByteArray) tag).getValues();
        }
        return new byte[0];
    }

    public short[] getShortArray(int index) {
        BsoTag tag;
        if (index >= 0 && index < this.size() && (tag = this.get(index)).getType() == BsoTypes.SHORT_ARRAY) {
            return ((BsoShortArray) tag).getValues();
        }
        return new short[0];
    }

    public int[] getIntArray(int index) {
        BsoTag tag;
        if (index >= 0 && index < this.size() && (tag = this.get(index)).getType() == BsoTypes.INT_ARRAY) {
            return ((BsoIntArray) tag).getValues();
        }
        return new int[0];
    }

    public int size() {
        return this.list.size();
    }

    public void clear() {
        this.list.clear();
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public BsoTag copy() {
        List<BsoTag> list = new ArrayList<>();
        for (BsoTag tag : this.list) {
            list.add(tag.copy());
        }
        return new BsoList(list);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return Objects.equals(this.list, ((BsoList) obj).list);
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }
}
