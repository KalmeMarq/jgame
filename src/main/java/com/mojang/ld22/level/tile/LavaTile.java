package com.mojang.ld22.level.tile;

import java.util.Random;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.level.Level;

public class LavaTile extends Tile {
    public LavaTile(int id) {
        super(id);
        this.connectsToSand = true;
        this.connectsToLava = true;
    }

    private final Random wRandom = new Random();

    public void render(Screen screen, Level level, int x, int y) {
        this.wRandom.setSeed((Tile.tickCount + (x / 2 - y) * 4311L) / 10 * 54687121L + x * 3271612L + y * 3412987161L);

        boolean u = !level.getTile(x, y - 1).connectsToLava;
        boolean d = !level.getTile(x, y + 1).connectsToLava;
        boolean l = !level.getTile(x - 1, y).connectsToLava;
        boolean r = !level.getTile(x + 1, y).connectsToLava;

        boolean su = u && level.getTile(x, y - 1).connectsToSand;
        boolean sd = d && level.getTile(x, y + 1).connectsToSand;
        boolean sl = l && level.getTile(x - 1, y).connectsToSand;
        boolean sr = r && level.getTile(x + 1, y).connectsToSand;

        if (!u && !l) {
            screen.renderSprite(x * 16, y * 16, this.wRandom.nextInt(4) + 9 * 32 + 28, 2, this.wRandom.nextInt(4));
        } else {
            screen.renderSprite(x * 16, y * 16, (l ? 20 : 21) + (u ? 0 : 1) * 32 + 11 * 32 + 3, 2, 0);
            if (su || sl) {
                screen.renderSprite(x * 16, y * 16, (l ? 23 : 24) + (u ? 0 : 1) * 32 + 14 * 32, 2, 0);
            } else {
                screen.renderSprite(x * 16, y * 16, (l ? 20 : 21) + (u ? 0 : 1) * 32 + 14 * 32, 2, 0);
            }
        }

        if (!u && !r) {
            screen.renderSprite(x * 16 + 8, y * 16, this.wRandom.nextInt(4) + 9 * 32 + 28, 2, this.wRandom.nextInt(4));
        } else {
            screen.renderSprite(x * 16 + 8, y * 16, (r ? 22 : 21) + (u ? 0 : 1) * 32 + 11 * 32 + 3, 2, 0);
            if (su || sr) {
                screen.renderSprite(x * 16 + 8, y * 16, (r ? 25 : 24) + (u ? 0 : 1) * 32 + 14 * 32, 2, 0);
            } else {
                screen.renderSprite(x * 16 + 8, y * 16, (r ? 22 : 21) + (u ? 0 : 1) * 32 + 14 * 32, 2, 0);
            }
        }

        if (!d && !l) {
            screen.renderSprite(x * 16, y * 16 + 8, this.wRandom.nextInt(4) + 9 * 32 + 28, 2, this.wRandom.nextInt(4));
        } else {
            screen.renderSprite(x * 16, y * 16 + 8, (l ? 20 : 21) + (d ? 2 : 1) * 32 + 11 * 32 + 3, 2, 0);
            if (sd || sl) {
                screen.renderSprite(x * 16, y * 16 + 8, (l ? 23 : 24) + (d ? 2 : 1) * 32 + 14 * 32, 2, 0);
            } else {
                screen.renderSprite(x * 16, y * 16 + 8, (l ? 20 : 21) + (d ? 2 : 1) * 32 + 14 * 32, 2, 0);
            }
        }
        if (!d && !r) {
            screen.renderSprite(x * 16 + 8, y * 16 + 8, this.wRandom.nextInt(4) + 9 * 32 + 28, 2, this.wRandom.nextInt(4));
        } else {
            screen.renderSprite(x * 16 + 8, y * 16 + 8, (r ? 22 : 21) + (d ? 2 : 1) * 32 + 11 * 32 + 3, 2, 0);
            if (sd || sr) {
                screen.renderSprite(x * 16 + 8, y * 16 + 8, (r ? 25 : 24) + (d ? 2 : 1) * 32 + 14 * 32, 2, 0);
            } else {
                screen.renderSprite(x * 16 + 8, y * 16 + 8, (r ? 22 : 21) + (d ? 2 : 1) * 32 + 14 * 32, 2, 0);
            }
        }
    }

    public boolean mayPass(Level level, int x, int y, Entity e) {
        return e.canSwim();
    }

    public void tick(Level level, int xt, int yt) {
        int xn = xt;
        int yn = yt;

        if (this.random.nextBoolean()) {
            xn += this.random.nextInt(2) * 2 - 1;
        } else {
            yn += this.random.nextInt(2) * 2 - 1;
        }

        if (level.getTile(xn, yn) == Tile.hole) {
            level.setTile(xn, yn, this, 0);
        }
    }

    public int getLightRadius(Level level, int x, int y) {
        return 6;
    }
}
