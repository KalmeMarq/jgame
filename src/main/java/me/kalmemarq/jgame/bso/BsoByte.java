package me.kalmemarq.jgame.bso;

import java.io.DataOutput;
import java.io.IOException;

public class BsoByte extends BsoTag {
    protected final byte value;

    public BsoByte(byte value) {
        this.value = value;
    }

    @Override
    public BsoType<BsoByte> getType() {
        return BsoTypes.BYTE;
    }

    @Override
    public void write(DataOutput output, boolean withAdditionalData) throws IOException {
        this.getType().write(output, this, withAdditionalData ? this.getAdditionalData() : 0);
    }

    public boolean booleanValue() {
        return this.value != 0;
    }

    public byte byteValue() {
        return this.value;
    }

    public short shortValue() {
        return this.value;
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
    public BsoByte copy() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof BsoByte && this.value == ((BsoByte) obj).value);
    }

    @Override
    public int hashCode() {
        return this.value;
    }
}
