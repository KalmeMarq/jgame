package me.kalmemarq.jgame.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class BsoByteArray extends BsoTag {
    private static final boolean DO_STUPID = false;
    private final byte[] values;

    public BsoByteArray(byte[] values) {
        this.values = values;
    }

    @Override
    public BsoType<BsoByteArray> getType() {
        return BsoTypes.BYTE_ARRAY;
    }

    @Override
    public void write(DataOutput output, boolean withAdditionalData) throws IOException {
        this.getType().write(output, this, withAdditionalData ? this.getAdditionalData() : 0);
    }

    @Override
    public byte getAdditionalData() {
        if (!BsoByteArray.DO_STUPID) return BsoTag.getAdditionalDataFromIntegerRange(this.size());

        boolean isAllInHalfPositiveByte = true;
        for (byte value : this.values) {
            if (value < 0 || value > 15) {
                isAllInHalfPositiveByte = false;
                break;
            }
        }
        return (byte) (BsoTag.getAdditionalDataFromIntegerRange(this.values.length) + (isAllInHalfPositiveByte ? 4 : 0));
    }

    public byte[] getValues() {
        return this.values;
    }

    public int size() {
        return this.values.length;
    }

    @Override
    public BsoByteArray copy() {
        final byte[] array = new byte[this.values.length];
        System.arraycopy(this.values, 0, array, 0, this.values.length);
        return new BsoByteArray(array);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof BsoByteArray && Arrays.equals(this.values, ((BsoByteArray) obj).values));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.values);
    }
}
