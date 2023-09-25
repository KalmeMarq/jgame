package me.kalmemarq.jgame.bso;

import java.io.DataOutput;
import java.io.IOException;

public class BsoFloat extends BsoTag {
    private final float value;

    public BsoFloat(float value) {
        this.value = value;
    }

    @Override
    public BsoType<BsoFloat> getType() {
        return BsoTypes.FLOAT;
    }

    @Override
    public void write(DataOutput output, boolean withAdditionalData) throws IOException {
        this.getType().write(output, this, withAdditionalData ? this.getAdditionalData() : 0);
    }

    public byte byteValue() {
        return (byte) ((int) Math.floor(this.value) & 0xFF);
    }

    public short shortValue() {
        return (short) ((int) Math.floor(this.value) & 0xFFFF);
    }

    public int intValue() {
        return (int) Math.floor(this.value);
    }

    public long longValue() {
        return (long) this.value;
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
    public BsoFloat copy() {
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
        return Float.compare(this.value, ((BsoFloat) obj).value) == 0;
    }

    @Override
    public int hashCode() {
        return (this.value != 0.0f ? Float.floatToIntBits(this.value) : 0);
    }
}
