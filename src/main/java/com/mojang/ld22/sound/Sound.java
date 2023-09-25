package com.mojang.ld22.sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
    public static final Sound playerHurt = new Sound("/playerhurt.wav");
    public static final Sound playerDeath = new Sound("/death.wav");
    public static final Sound monsterHurt = new Sound("/monsterhurt.wav");
    public static final Sound test = new Sound("/test.wav");
    public static final Sound pickup = new Sound("/pickup.wav");
    public static final Sound bossdeath = new Sound("/bossdeath.wav");
    public static final Sound craft = new Sound("/craft.wav");

    private final Clip[] clip = new Clip[5];

    private Sound(String name) {

        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Sound.class.getResource(name));
            for (int i = 0; i < 5; ++i) {
                this.clip[i] = AudioSystem.getClip();
                this.clip[i].open(audioInputStream);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            new Thread() {
                public void run() {
                    Clip clip = null;

                    for (int i = 0; i < 5; ++i) {
                        if (!Sound.this.clip[i].isRunning()) {
                            clip = Sound.this.clip[i];
                            break;
                        }
                    }

                    if (clip == null) {
                        clip = Sound.this.clip[0];
                        clip.stop();
                    }

                    clip.setFramePosition(0);
                    clip.start();
                }
            }.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
