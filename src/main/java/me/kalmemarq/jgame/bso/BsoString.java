package me.kalmemarq.jgame.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class BsoString extends BsoTag {
    private final String value;

    public BsoString(String value) {
        this.value = Objects.requireNonNull(value, "String must not be null");
    }

    @Override
    public BsoType<BsoString> getType() {
        return BsoTypes.STRING;
    }

    @Override
    public void write(DataOutput output, boolean withAdditionalData) throws IOException {
        this.getType().write(output, this, withAdditionalData ? this.getAdditionalData() : 0);
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public BsoTag copy() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return Objects.equals(this.value, ((BsoString) obj).value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}
