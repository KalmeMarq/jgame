package me.kalmemarq.jgame.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class BsoFloatArray extends BsoTag {
    private final float[] values;

    public BsoFloatArray(float[] values) {
        this.values = values;
    }

    @Override
    public BsoType<BsoFloatArray> getType() {
        return BsoTypes.FLOAT_ARRAY;
    }

    @Override
    public void write(DataOutput output, boolean withAdditionalData) throws IOException {
        this.getType().write(output, this, withAdditionalData ? this.getAdditionalData() : 0);
    }

    @Override
    public byte getAdditionalData() {
        return BsoTag.getAdditionalDataFromIntegerRange(this.size());
    }

    public float[] getValues() {
        return this.values;
    }

    public int size() {
        return this.values.length;
    }

    @Override
    public BsoFloatArray copy() {
        final float[] array = new float[this.values.length];
        System.arraycopy(this.values, 0, array, 0, this.values.length);
        return new BsoFloatArray(array);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof BsoFloatArray && Arrays.equals(this.values, ((BsoFloatArray) obj).values));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.values);
    }
}
