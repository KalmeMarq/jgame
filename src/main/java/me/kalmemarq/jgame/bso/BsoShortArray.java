package me.kalmemarq.jgame.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class BsoShortArray extends BsoTag {
    private final short[] values;

    public BsoShortArray(short[] values) {
        this.values = values;
    }

    @Override
    public BsoType<BsoShortArray> getType() {
        return BsoTypes.SHORT_ARRAY;
    }

    @Override
    public void write(DataOutput output, boolean withAdditionalData) throws IOException {
        this.getType().write(output, this, withAdditionalData ? this.getAdditionalData() : 0);
    }

    @Override
    public byte getAdditionalData() {
        boolean isInByteRange = true;
        for (short value : this.values) {
            if (BsoTag.getAdditionalDataFromIntegerRange(value) == 0) {
                isInByteRange = false;
                break;
            }
        }
        return (byte) (BsoTag.getAdditionalDataFromIntegerRange(this.size()) + (isInByteRange ? 4 : 0));
    }

    public short[] getValues() {
        return this.values;
    }

    public int size() {
        return this.values.length;
    }

    @Override
    public BsoShortArray copy() {
        final short[] array = new short[this.values.length];
        System.arraycopy(this.values, 0, array, 0, this.values.length);
        return new BsoShortArray(array);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return Arrays.equals(this.values, ((BsoShortArray) obj).values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.values);
    }
}
