package me.kalmemarq.jgame.resource;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public interface ResourcePack {
    @Nullable
    PackResource get(String path);
    boolean has(String path);

    List<PackResource> getFiles(String path, Predicate<String> namePredicate);
}
