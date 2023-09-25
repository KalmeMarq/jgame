package me.kalmemarq.jgame.bso;

public class BsoUInt extends BsoInt {
    public BsoUInt(long value) {
        super((int) (value & 0xffffffffL));
    }

    @Override
    public byte getAdditionalData() {
        return (byte) (super.getAdditionalData() + 4);
    }

    public long unsignedIntValue() {
        return this.intValue() & 0x00000000ffffffffL;
    }
}
