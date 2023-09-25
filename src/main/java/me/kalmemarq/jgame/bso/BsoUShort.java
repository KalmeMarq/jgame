package me.kalmemarq.jgame.bso;

public class BsoUShort extends BsoShort {
    public BsoUShort(int value) {
        super((short) (value & 0xffff));
    }

    @Override
    public byte getAdditionalData() {
        return (byte) (super.getAdditionalData() + 4);
    }

    public int unsignedShortValue() {
        return this.shortValue() & 0x0000ffff;
    }
}
