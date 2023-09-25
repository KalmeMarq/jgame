package me.kalmemarq.jgame.bso;

public class BsoBoolean extends BsoByte {
    private final boolean isBoolean;

    public BsoBoolean(boolean value) {
        super((byte) (value ? 1 : 0));
        this.isBoolean = true;
    }

    @Override
    public byte getAdditionalData() {
        return (byte) (this.isBoolean ? this.value == 1 ? 5 : 4 : 0);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof BsoBoolean && this.value == ((BsoByte) obj).value);
    }
}
