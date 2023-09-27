package me.kalmemarq.jgame.resource;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipResourcePack implements ResourcePack {
    @Nullable
    protected ZipFile zipFile;
    protected boolean failedToOpen;

    private Path filePath;

    public ZipResourcePack(Path zipFilePath) {
        this.filePath = zipFilePath;
    }

    @Nullable
    protected ZipFile getZipFile() {
        if (this.zipFile == null && !this.failedToOpen) {
            try {
                this.zipFile = new ZipFile(this.filePath.toFile());
            } catch(IOException e) {
                this.failedToOpen = true;
            }
        }

        return this.zipFile;
    }

    @Nullable
    @Override
    public PackResource get(String path) {
        ZipFile zip = this.getZipFile();
        if (zip != null) {
            ZipEntry entry = zip.getEntry(path);

            if (entry != null) {
                return new PackResource(path, () -> zip.getInputStream(entry));
            }
        }
        return null;
    }

    @Override
    public boolean has(String path) {
        ZipFile zip = this.getZipFile();
        if (zip != null) {
            ZipEntry entry = zip.getEntry(path);
            return entry != null;
        }
        return false;
    }

    @Override
    public List<PackResource> getFiles(String path, Predicate<String> namePredicate) {
        return Collections.emptyList();
    }
}
