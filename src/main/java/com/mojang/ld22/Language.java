package com.mojang.ld22;

import me.kalmemarq.jgame.resource.PackResource;
import me.kalmemarq.jgame.resource.PackResources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Language {
    private static final Properties translations = new Properties();

    public static void load(PackResources resourcePack) {
        translations.clear();

        PackResource langFile = resourcePack.get("en_us.lang");

        if (langFile != null) {
            try (InputStream inputStream = langFile.getInputStream()) {
                translations.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("en_us.lang not found");
        }
    }

    public static String translate(String key) {
        return translations.getProperty(key, key);
    }
}
