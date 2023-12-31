package com.mojang.ld22.entity;

import java.util.List;

import com.mojang.ld22.gfx.Screen;

public class Spark extends Entity {
    private int lifeTime;
    public double xa, ya;
    public double xx, yy;
    private int time;
    private AirWizard owner;

    public Spark() {
    }

    public Spark(AirWizard owner, double xa, double ya) {
        this.owner = owner;
        this.xx = this.x = owner.x;
        this.yy = this.y = owner.y;
        this.xr = 0;
        this.yr = 0;

        this.xa = xa;
        this.ya = ya;

        this.lifeTime = 60 * 10 + this.random.nextInt(30);
    }

    public void tick() {
        this.time++;
        if (this.time >= this.lifeTime) {
            remove();
            return;
        }
        this.xx += this.xa;
        this.yy += this.ya;
        this.x = (int) this.xx;
        this.y = (int) this.yy;
        List<Entity> toHit = this.level.getEntities(this.x, this.y, this.x, this.y);
        for (Entity e : toHit) {
            if (e instanceof Mob && !(e instanceof AirWizard)) {
                e.hurt(this.owner, 1, ((Mob) e).dir ^ 1);
            }
        }
    }

    public boolean isBlockableBy(Mob mob) {
        return false;
    }

    public void render(Screen screen) {
        if (this.time >= this.lifeTime - 6 * 20) {
            if (this.time / 6 % 2 == 0) {
                return;
            }
        }
        screen.renderTextured(this.x - 4, this.y - 4 - 2 + 12, 8, 8, 24, 104, 2, 0xFFFFFF, this.random.nextInt(4));
        screen.renderTextured(this.x - 4, this.y - 4 + 2, 8, 8, 24, 104, 2, 0x000000, this.random.nextInt(4));
    }
}
