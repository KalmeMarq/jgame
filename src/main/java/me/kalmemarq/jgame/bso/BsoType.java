package me.kalmemarq.jgame.bso;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class BsoType<T extends BsoTag> {
    abstract public byte getId();
    abstract public T read(DataInput input, byte additionalData) throws IOException;
    abstract public void write(DataOutput output, T tag, byte additionalData) throws IOException;
}
