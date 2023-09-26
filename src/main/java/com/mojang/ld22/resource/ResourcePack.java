package com.mojang.ld22.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;

public interface ResourcePack {
    InputSupplier get(String path);
    boolean has(String path);

    List<InputSupplier> getFiles(String path, Predicate<String> namePredicate);

    public interface InputSupplier {
        InputStream get() throws IOException;
    }
}
