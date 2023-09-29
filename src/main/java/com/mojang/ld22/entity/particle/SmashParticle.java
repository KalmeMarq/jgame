package com.mojang.ld22.entity.particle;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.sound.Sound;

public class SmashParticle extends Entity {
    private int time = 0;

    public SmashParticle(int x, int y) {
        this.x = x;
        this.y = y;
        Sound.play(Sound.Event.MONSTER_HURT, 1.0f);
    }

    public void tick() {
        this.time++;
        if (this.time > 10) {
            remove();
        }
    }

    public void render(Screen screen) {
        screen.renderSprite(this.x - 8, this.y - 8, 12 * 32, 2, 2);
        screen.renderSprite(this.x, this.y - 8, 12 * 32, 2, 3);
        screen.renderSprite(this.x - 8, this.y, 12 * 32, 2, 0);
        screen.renderSprite(this.x, this.y, 12 * 32, 2, 1);
    }
}
