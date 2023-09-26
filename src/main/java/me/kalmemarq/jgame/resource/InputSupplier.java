package me.kalmemarq.jgame.resource;

import java.io.IOException;
import java.io.InputStream;

public interface InputSupplier {
    InputStream get() throws IOException;
}
