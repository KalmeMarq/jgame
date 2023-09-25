package me.kalmemarq.jgame.bso;

public class BsoUByte extends BsoByte {
    public BsoUByte(short value) {
        super((byte) (value & 0xff));
    }

    @Override
    public byte getAdditionalData() {
        return (byte) (super.getAdditionalData() + 4);
    }

    public short unsignedByteValue() {
        return (short) (this.byteValue() & 0x00ff);
    }
}
