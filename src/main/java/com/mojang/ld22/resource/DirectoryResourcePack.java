package com.mojang.ld22.resource;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DirectoryResourcePack implements ResourcePack {
    private final Path root;

    public DirectoryResourcePack(Path root) {
        this.root = root;
    }

    @Nullable
    @Override
    public PackResource get(String path) {
        return new PackResource(path, () -> Files.newInputStream(this.root.resolve(path)));
    }

    @Override
    public boolean has(String path) {
        return Files.exists(this.root.resolve(path));
    }

    @Override
    public List<PackResource> getFiles(String path, Predicate<String> namePredicate) {
        List<PackResource> list = new ArrayList<>();
        try (Stream<Path> stream = Files.list(this.root.resolve(path))) {
            for (Path file : stream.toList()) {
                if (namePredicate.test(file.getFileName().toString())) list.add(new PackResource(file.getFileName().toString(), () -> Files.newInputStream(file)));
            }
        } catch (IOException ignored) {
        }
        return list;
    }
}
