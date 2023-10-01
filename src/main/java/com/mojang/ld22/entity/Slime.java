package com.mojang.ld22.entity;

import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.resource.Resource;

public class Slime extends Mob {
    private int xa, ya;
    private int jumpTime = 0;
    private final int lvl;

    public Slime(int lvl) {
        this.lvl = lvl;
        this.x = this.random.nextInt(64 * 16);
        this.y = this.random.nextInt(64 * 16);
        this.health = this.maxHealth = lvl * lvl * 5;
    }

    public void tick() {
        super.tick();

        int speed = 1;
        if (!move(this.xa * speed, this.ya * speed) || this.random.nextInt(40) == 0) {
            if (this.jumpTime <= -10) {
                this.xa = (this.random.nextInt(3) - 1);
                this.ya = (this.random.nextInt(3) - 1);

                if (this.level.player != null) {
                    int xd = this.level.player.x - this.x;
                    int yd = this.level.player.y - this.y;
                    if (xd * xd + yd * yd < 50 * 50) {
                        if (xd < 0) {
                            this.xa = -1;
                        }
                        if (xd > 0) {
                            this.xa = +1;
                        }
                        if (yd < 0) {
                            this.ya = -1;
                        }
                        if (yd > 0) {
                            this.ya = +1;
                        }
                    }
                }

                if (this.xa != 0 || this.ya != 0) {
                    this.jumpTime = 10;
                }
            }
        }

        this.jumpTime--;
        if (this.jumpTime == 0) {
            this.xa = this.ya = 0;
        }
    }

    protected void die() {
        super.die();

        int count = this.random.nextInt(2) + 1;
        for (int i = 0; i < count; i++) {
            this.level.add(new ItemEntity(new ResourceItem(Resource.slime), this.x + this.random.nextInt(11) - 5, this.y + this.random.nextInt(11) - 5));
        }

        if (this.level.player != null) {
            this.level.player.score += 25 * this.lvl;
        }
    }

    public void render(Screen screen) {
        int xt = 0;
        if (this.lvl == 2) {
            xt = 32;
        } else if (this.lvl == 3) {
            xt = 64;
        } else if (this.lvl == 4) {
            xt = 96;
        }

        int xo = this.x - 8;
        int yo = this.y - 11;

        if (this.jumpTime > 0) {
            xt += 16;
            yo -= 4;
        }

        screen.renderTextured(xo, yo, 16, 16, xt, 240, 2, 0xFFFFFF, this.hurtTime > 0, 0);
    }

    protected void touchedBy(Entity entity) {
//        if (entity instanceof Player) {
//            entity.hurt(this, this.lvl, this.dir);
//        }
    }
}
