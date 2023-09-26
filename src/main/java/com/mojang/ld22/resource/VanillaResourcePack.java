package com.mojang.ld22.resource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;

public class VanillaResourcePack extends DirectoryResourcePack {
    private static Path jarPath;

    public VanillaResourcePack() {
        super(VanillaResourcePack.jarPath);
    }

    static {
        try {
            jarPath = Path.of(Objects.requireNonNull(VanillaResourcePack.class.getResource("/.root")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (FileSystemNotFoundException e) {
            try {
                FileSystem fs = FileSystems.newFileSystem(VanillaResourcePack.class.getResource("/.root").toURI(), Collections.emptyMap());
                jarPath = fs.getPath("/.root");
            } catch (URISyntaxException | IOException e1) {
                throw new RuntimeException(e1);
            }
        }
        jarPath = jarPath.getParent();
    }
}
