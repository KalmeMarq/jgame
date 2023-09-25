package me.kalmemarq.jgame.bso;

import java.io.DataOutput;
import java.io.IOException;

public class BsoDouble extends BsoTag {
    private final double value;

    public BsoDouble(double value) {
        this.value = value;
    }

    @Override
    public BsoType<BsoDouble> getType() {
        return BsoTypes.DOUBLE;
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
        return (float) this.value;
    }

    public double doubleValue() {
        return this.value;
    }

    public Number numberValue() {
        return this.value;
    }

    @Override
    public BsoDouble copy() {
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
        return Double.compare(this.value, ((BsoDouble) obj).value) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(this.value);
        return (int) (temp ^ (temp >>> 32));
    }
}
