package com.mojang.ld22.resource;

import java.io.IOException;
import java.io.InputStream;

public interface InputSupplier {
    InputStream get() throws IOException;
}
