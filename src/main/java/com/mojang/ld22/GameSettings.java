package com.mojang.ld22;

import me.kalmemarq.jgame.StringHelper;
import me.kalmemarq.jgame.logging.LogManager;
import me.kalmemarq.jgame.logging.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GameSettings {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path settingsPath;
    public boolean sound;

    public GameSettings(Path savePath) {
        this.settingsPath =  savePath.resolve("settings.txt");
    }

    public void load() {
        if (!Files.exists(this.settingsPath)) return;
        try {
            List<String> settingsContent = StringHelper.readLines(Files.newInputStream(this.settingsPath));
            for (String line : settingsContent) {
                String[] l = line.split("=");
                if (l.length == 2) {
                    if (l[0].equals("sound")) {
                        this.sound = "true".equals(l[1]);
                    }
                }
            }
            LOGGER.info("Loaded settings successfully");
        } catch (IOException e) {
            LOGGER.error("Failed to load settings", e);
        }
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(this.settingsPath, StandardCharsets.UTF_8)) {
            writer.write("sound=" + this.sound + "\n");
            writer.flush();
            LOGGER.info("Saved settings successfully");
        } catch (Exception e) {
            LOGGER.warn("Failed to save settings", e);
        }
    }
}
