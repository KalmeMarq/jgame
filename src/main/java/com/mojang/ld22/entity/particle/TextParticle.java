package com.mojang.ld22.entity.particle;

import com.mojang.ld22.Game;
import com.mojang.ld22.gfx.Screen;

public class TextParticle extends Particle {
    private final String message;
    private final int textColor;
    public double xa, ya, za;
    public double xx, yy, zz;

    public TextParticle(String message, int x, int y, int textColor) {
        this.message = message;
        this.x = x;
        this.y = y;
        this.textColor = textColor;
        this.xx = x;
        this.yy = y;
        this.zz = 2;
        this.xa = this.random.nextGaussian() * 0.3;
        this.ya = this.random.nextGaussian() * 0.2;
        this.za = this.random.nextFloat() * 0.7 + 2;
    }

    public void tick() {
        super.tick();
        if (this.time > 60) {
            this.remove();
        }
        this.xx += this.xa;
        this.yy += this.ya;
        this.zz += this.za;
        if (this.zz < 0) {
            this.zz = 0;
            this.za *= -0.5;
            this.xa *= 0.6;
            this.ya *= 0.6;
        }
        this.za -= 0.15;
        this.x = (int) this.xx;
        this.y = (int) this.yy;
    }

    public void render(Screen screen) {
        Game.getInstance().font.draw(this.message, screen, this.x - this.message.length() * 4 + 1, this.y - (int) (this.zz) + 1, 0x0A0A0A);
        Game.getInstance().font.draw(this.message, screen, this.x - this.message.length() * 4, this.y - (int) (this.zz), this.textColor);
    }

}
