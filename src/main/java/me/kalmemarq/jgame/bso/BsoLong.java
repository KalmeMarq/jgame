package me.kalmemarq.jgame.bso;

import java.io.DataOutput;
import java.io.IOException;

public class BsoLong extends BsoTag {
    private final long value;

    public BsoLong(long value) {
        this.value = value;
    }

    @Override
    public BsoType<BsoLong> getType() {
        return BsoTypes.LONG;
    }

    @Override
    public void write(DataOutput output, boolean withAdditionalData) throws IOException {
        this.getType().write(output, this, withAdditionalData ? this.getAdditionalData() : 0);
    }

    @Override
    public byte getAdditionalData() {
        return BsoTag.getAdditionalDataFromIntegerRange(this.value);
    }

    public byte byteValue() {
        return (byte) (this.value & 0xFFL);
    }

    public short shortValue() {
        return (short) (this.value & 0xFFFFL);
    }

    public int intValue() {
        return (int) (this.value & 0xFFFFFFFFL);
    }

    public long longValue() {
        return this.value;
    }

    public float floatValue() {
        return this.value;
    }

    public double doubleValue() {
        return this.value;
    }

    public Number numberValue() {
        return this.value;
    }

    @Override
    public BsoTag copy() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return this.value == ((BsoLong) obj).value;
    }

    @Override
    public int hashCode() {
        return (int) (this.value ^ (this.value >>> 32));
    }
}
