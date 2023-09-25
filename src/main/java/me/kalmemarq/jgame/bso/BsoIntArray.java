package me.kalmemarq.jgame.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class BsoIntArray extends BsoTag {
    private final int[] values;

    public BsoIntArray(int[] values) {
        this.values = values;
    }

    @Override
    public BsoType<BsoIntArray> getType() {
        return BsoTypes.INT_ARRAY;
    }

    @Override
    public void write(DataOutput output, boolean withAdditionalData) throws IOException {
        this.getType().write(output, this, withAdditionalData ? this.getAdditionalData() : 0);
    }

    @Override
    public byte getAdditionalData() {
        byte isInByteRange = 2;
        for (int value : this.values) {
            int range = BsoTag.getAdditionalDataFromIntegerRange(value);
            if (isInByteRange == 2 && range < 2) {
                isInByteRange = 1;
            } else if (isInByteRange == 1 && range == 0) {
                isInByteRange = 0;
                break;
            }
        }
        return (byte) (BsoTag.getAdditionalDataFromIntegerRange(this.size()) + (isInByteRange * 4));
    }

    public int[] getValues() {
        return this.values;
    }

    public int size() {
        return this.values.length;
    }

    @Override
    public BsoIntArray copy() {
        final int[] array = new int[this.values.length];
        System.arraycopy(this.values, 0, array, 0, this.values.length);
        return new BsoIntArray(array);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Arrays.equals(this.values, ((BsoIntArray) o).values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.values);
    }
}
