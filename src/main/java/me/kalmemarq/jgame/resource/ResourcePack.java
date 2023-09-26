package me.kalmemarq.jgame.resource;

import java.util.List;
import java.util.function.Predicate;

public interface ResourcePack {
    PackResource get(String path);
    boolean has(String path);

    List<PackResource> getFiles(String path, Predicate<String> namePredicate);
}
