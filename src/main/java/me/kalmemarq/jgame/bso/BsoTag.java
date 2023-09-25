package me.kalmemarq.jgame.bso;

import java.io.DataOutput;
import java.io.IOException;

public abstract class BsoTag {
    abstract public BsoType<?> getType();

    public byte getAdditionalData() {
        return 0;
    }

    public int getIdWithAdditionalData() {
        return this.getAdditionalData() << 4 | this.getType().getId();
    }

    public void write(DataOutput output) throws IOException {
        this.write(output, true);
    }

    abstract public void write(DataOutput output, boolean withAdditionalData) throws IOException;

    @Override
    public String toString() {
        return BsoHelper.writeAsString(this);
    }

    abstract public BsoTag copy();

    protected static byte getAdditionalDataFromIntegerRange(short value) {
        return (byte) (value <= Byte.MAX_VALUE && value >= Byte.MIN_VALUE ? 1 : 0);
    }

    protected static byte getAdditionalDataFromIntegerRange(int value) {
        byte ad = 0;
        if (value <= Byte.MAX_VALUE && value >= Byte.MIN_VALUE) {
            ad = 2;
        } else if (value <= Short.MAX_VALUE && value >= Short.MIN_VALUE) {
            ad = 1;
        }
        return ad;
    }

    protected static byte getAdditionalDataFromIntegerRange(long value) {
        byte ad = 0;
        if (value <= Byte.MAX_VALUE && value >= Byte.MIN_VALUE) {
            ad = 3;
        } else if (value <= Short.MAX_VALUE && value >= Short.MIN_VALUE) {
            ad = 2;
        } else if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
            ad = 1;
        }
        return ad;
    }
}
