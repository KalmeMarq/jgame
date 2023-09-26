package me.kalmemarq.jgame.resource;

import java.io.IOException;
import java.io.InputStream;

public class PackResource {
    private final InputSupplier inputSupplier;
    private final String path;

    public PackResource(String path, InputSupplier inputSupplier) {
        this.path = path;
        this.inputSupplier = inputSupplier;
    }

    public InputStream getInputStream() throws IOException {
        return this.inputSupplier.get();
    }

    public String getPath() {
        return this.path;
    }
}
