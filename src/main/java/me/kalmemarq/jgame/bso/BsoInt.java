package me.kalmemarq.jgame.bso;

import java.io.DataOutput;
import java.io.IOException;

public class BsoInt extends BsoTag {
    private final int value;

    public BsoInt(int value) {
        this.value = value;
    }

    @Override
    public BsoType<BsoInt> getType() {
        return BsoTypes.INT;
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
        return (byte) (this.value & 0xFF);
    }

    public short shortValue() {
        return (short) (this.value & 0xFFFF);
    }

    public int intValue() {
        return this.value;
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
    public BsoInt copy() {
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
        return this.value == ((BsoInt) obj).value;
    }

    @Override
    public int hashCode() {
        return this.value;
    }
}
