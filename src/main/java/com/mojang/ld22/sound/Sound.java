package com.mojang.ld22.sound;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mojang.ld22.Game;
import me.kalmemarq.jgame.JsonHelper;
import me.kalmemarq.jgame.logging.LogManager;
import me.kalmemarq.jgame.logging.Logger;
import me.kalmemarq.jgame.resource.PackResource;
import me.kalmemarq.jgame.resource.PackResources;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class Sound {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, Supplier<BufferedInputStream>> map0 = new HashMap<>();
    public static final List<Clip> clips = new ArrayList<>();

    private Sound() {
    }

    public static void load(PackResources resourcePack) {
        clips.forEach(Clip::close);
        clips.clear();
        PackResource resource = resourcePack.get("sounds.json");
        if (resource != null) {
            try (InputStream inputStream = resource.getInputStream()) {
                ObjectNode json = (ObjectNode) JsonHelper.OBJECT_MAPPER.readTree(inputStream);
                for (Map.Entry<String, JsonNode> item : json.properties()) {
                    ObjectNode obj = (ObjectNode) item.getValue();
                    String soundPath = obj.get("sound").textValue();
                    if (resourcePack.has("sounds/" + soundPath)) {
                        try {
                           Sound.map0.put(item.getKey(), () -> {
                               try {
                                   return new BufferedInputStream(Objects.requireNonNull(resourcePack.get("sounds/" + soundPath)).getInputStream());
                               } catch (IOException e) {
                                   throw new RuntimeException(e);
                               }
                           });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.info("Failed to load sounds.json", e);
            }
        }
    }

    public static void tick() {
        Iterator<Clip> clips = Sound.clips.iterator();
        while (clips.hasNext()) {
            Clip clip = clips.next();
            if (!clip.isRunning()) {
                clip.close();
                clips.remove();
            }
        }
    }

    public static void play(Sound.Event sound) {
        Sound.play(sound, 1.0f);
    }

    public static void play(Sound.Event sound, float volume) {
        if (!Game.getInstance().settings.sound) {
            return;
        }

        if (Sound.map0.get(sound.name()) != null) {
            Clip clip;
            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Sound.map0.get(sound.name()).get())) {
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
            } catch (Exception e) {
                LOGGER.error("Failed to load sound", e);
                return;
            }
            new Thread(() -> {
                FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue((float) (20.0f * Math.log10(volume)));
                clip.start();
            }).start();
            Sound.clips.add(clip);
        } else {
            LOGGER.warn("Sound '{}' is not registered", sound.name());
        }
    }

    public record Event(String name) {
        public static final Event DEATH = new Event("entity.generic.death");
        public static final Event AIR_WIZARD_DEATH = new Event("entity.air_wizard.death");
        public static final Event PLAYER_HURT = new Event("entity.player.hurt");
        public static final Event MONSTER_HURT = new Event("entity.monster.hurt");
        public static final Event TEST = new Event("random.test");
        public static final Event PICKUP = new Event("random.pickup");
        public static final Event CRAFT = new Event("random.craft");
    }
}
