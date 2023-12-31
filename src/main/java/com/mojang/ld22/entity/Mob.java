package com.mojang.ld22.entity;

import com.mojang.ld22.entity.particle.TextParticle;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;
import com.mojang.ld22.sound.Sound;
import me.kalmemarq.jgame.bso.BsoMap;

public class Mob extends Entity {
    protected int walkDist = 0;
    protected int dir = 0;
    public int hurtTime = 0;
    protected int xKnockback, yKnockback;
    public int maxHealth = 10;
    public int health = this.maxHealth;
    public int swimTimer = 0;
    public int tickTime = 0;

    public Mob() {
        this.x = this.y = 8;
        this.xr = 4;
        this.yr = 3;
    }

    @Override
    public void writeData(BsoMap map) {
        super.writeData(map);
        map.putInt("walkDist", this.walkDist);
        map.putInt("dir", this.dir);
        map.putInt("hurtTime", this.hurtTime);
        map.putInt("xKnockback", this.xKnockback);
        map.putInt("yKnockback", this.yKnockback);
        map.putInt("maxHealth", this.maxHealth);
        map.putInt("health", this.health);
        map.putInt("swimTimer", this.swimTimer);
    }

    @Override
    public void loadData(BsoMap map) {
        super.loadData(map);
        this.walkDist = map.getInt("walkDist");
        this.dir = map.getInt("dir");
        this.hurtTime = map.getInt("hurtTime");
        this.xKnockback = map.getInt("xKnockback");
        this.yKnockback = map.getInt("yKnockback");
        this.maxHealth = map.getInt("maxHealth");
        this.health = map.getInt("health");
        this.swimTimer = map.getInt("swimTimer");
    }

    public void tick() {
        this.tickTime++;
        if (this.level.getTile(this.x >> 4, this.y >> 4) == Tile.lava) {
            this.hurt(this, 4, this.dir ^ 1);
        }

        if (this.health <= 0) {
            this.die();
        }
        if (this.hurtTime > 0) {
            this.hurtTime--;
        }
    }

    protected void die() {
        this.remove();
    }

    public boolean move(int xa, int ya) {
        if (this.isSwimming()) {
            if (this.swimTimer++ % 2 == 0) {
                return true;
            }
        }
        if (this.xKnockback < 0) {
            this.move2(-1, 0);
            this.xKnockback++;
        }
        if (this.xKnockback > 0) {
            this.move2(1, 0);
            this.xKnockback--;
        }
        if (this.yKnockback < 0) {
            this.move2(0, -1);
            this.yKnockback++;
        }
        if (this.yKnockback > 0) {
            this.move2(0, 1);
            this.yKnockback--;
        }
        if (this.hurtTime > 0) {
            return true;
        }
        if (xa != 0 || ya != 0) {
            this.walkDist++;
            if (xa < 0) {
                this.dir = 2;
            }
            if (xa > 0) {
                this.dir = 3;
            }
            if (ya < 0) {
                this.dir = 1;
            }
            if (ya > 0) {
                this.dir = 0;
            }
        }
        return super.move(xa, ya);
    }

    protected boolean isSwimming() {
        Tile tile = this.level.getTile(this.x >> 4, this.y >> 4);
        return tile == Tile.water || tile == Tile.lava;
    }

    public boolean blocks(Entity e) {
        return e.isBlockableBy(this);
    }

    public void hurt(Tile tile, int x, int y, int damage) {
        int attackDir = this.dir ^ 1;
        this.doHurt(damage, attackDir);
    }

    public void hurt(Mob mob, int damage, int attackDir) {
        this.doHurt(damage, attackDir);
    }

    public void heal(int heal) {
        if (this.hurtTime > 0) {
            return;
        }

        this.level.add(new TextParticle(String.valueOf(heal), this.x, this.y, 0x4DC04D));
        this.health += heal;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;
        }
    }

    protected void doHurt(int damage, int attackDir) {
        if (this.hurtTime > 0) {
            return;
        }

        if (this.level.player != null) {
            int xd = this.level.player.x - this.x;
            int yd = this.level.player.y - this.y;
            if (xd * xd + yd * yd < 80 * 80) {
                Sound.play(Sound.Event.MONSTER_HURT, 1.0f);
            }
        }
        this.level.add(new TextParticle(String.valueOf(damage), this.x, this.y, 0x9E2C2C));
        this.health -= damage;
        if (attackDir == 0) {
            this.yKnockback = +6;
        }
        if (attackDir == 1) {
            this.yKnockback = -6;
        }
        if (attackDir == 2) {
            this.xKnockback = -6;
        }
        if (attackDir == 3) {
            this.xKnockback = +6;
        }
        this.hurtTime = 10;
    }

    public boolean findStartPos(Level level) {
        int x = this.random.nextInt(level.w);
        int y = this.random.nextInt(level.h);
        int xx = x * 16 + 8;
        int yy = y * 16 + 8;

        if (level.player != null) {
            int xd = level.player.x - xx;
            int yd = level.player.y - yy;
            if (xd * xd + yd * yd < 80 * 80) {
                return false;
            }
        }

        int r = level.monsterDensity * 16;
        if (!level.getEntities(xx - r, yy - r, xx + r, yy + r).isEmpty()) {
            return false;
        }

        if (level.getTile(x, y).mayPass(level, x, y, this)) {
            this.x = xx;
            this.y = yy;
            return true;
        }

        return false;
    }
}
