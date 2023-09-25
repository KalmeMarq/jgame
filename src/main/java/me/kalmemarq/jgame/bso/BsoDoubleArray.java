package me.kalmemarq.jgame.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class BsoDoubleArray extends BsoTag {
    private final double[] values;

    public BsoDoubleArray(double[] values) {
        this.values = values;
    }

    @Override
    public BsoType<BsoDoubleArray> getType() {
        return BsoTypes.DOUBLE_ARRAY;
    }

    @Override
    public void write(DataOutput output, boolean withAdditionalData) throws IOException {
        this.getType().write(output, this, withAdditionalData ? this.getAdditionalData() : 0);
    }

    @Override
    public byte getAdditionalData() {
        return BsoTag.getAdditionalDataFromIntegerRange(this.size());
    }

    public double[] getValues() {
        return this.values;
    }

    public int size() {
        return this.values.length;
    }

    @Override
    public BsoDoubleArray copy() {
        final double[] array = new double[this.values.length];
        System.arraycopy(this.values, 0, array, 0, this.values.length);
        return new BsoDoubleArray(array);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof BsoDoubleArray && Arrays.equals(this.values, ((BsoDoubleArray) obj).values));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.values);
    }
}
