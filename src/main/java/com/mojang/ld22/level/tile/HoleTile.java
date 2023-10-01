package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.level.Level;

public class HoleTile extends Tile {
    public HoleTile(int id) {
        super(id);
        this.connectsToSand = true;
        this.connectsToWater = true;
        this.connectsToLava = true;
    }

    public void render(Screen screen, Level level, int x, int y) {
        boolean u = !level.getTile(x, y - 1).connectsToLiquid();
        boolean d = !level.getTile(x, y + 1).connectsToLiquid();
        boolean l = !level.getTile(x - 1, y).connectsToLiquid();
        boolean r = !level.getTile(x + 1, y).connectsToLiquid();

        boolean su = u && level.getTile(x, y - 1).connectsToSand;
        boolean sd = d && level.getTile(x, y + 1).connectsToSand;
        boolean sl = l && level.getTile(x - 1, y).connectsToSand;
        boolean sr = r && level.getTile(x + 1, y).connectsToSand;

        boolean isBellowOverworld = level.depth < 0;

        if (!u && !l) {
            screen.renderSprite(x * 16, y * 16, 12 * 32 + 28, 2, 0);
        } else {
            screen.renderSprite(x * 16, y * 16, (l ? 20 : 21) + (u ? 0 : 1) * 32 + 11 * 32 - 3, 2, 0);
            if (su || sl) {
                screen.renderSprite(x * 16, y * 16, (l ? 23 : 24) + (u ? 0 : 1) * 32 + 14 * 32, 2, 0);
            } else {
                screen.renderSprite(x * 16, y * 16, (l ? 20 : 21) + (u ? 0 : 1) * 32 + 14 * 32 + (isBellowOverworld ? -3 : 0), 2, 0);
            }
        }

        if (!u && !r) {
            screen.renderSprite(x * 16 + 8, y * 16, 1 + 12 * 32 + 28, 2, 0);
        } else {
            screen.renderSprite(x * 16 + 8, y * 16, (r ? 22 : 21) + (u ? 0 : 1) * 32 + 11 * 32 - 3, 2, 0);
            if (su || sr) {
                screen.renderSprite(x * 16 + 8, y * 16, (r ? 25 : 24) + (u ? 0 : 1) * 32 + 14 * 32, 2, 0);
            } else {
                screen.renderSprite(x * 16 + 8, y * 16, (r ? 22 : 21) + (u ? 0 : 1) * 32 + 14 * 32 + (isBellowOverworld ? -3 : 0), 2, 0);
            }
        }

        if (!d && !l) {
            screen.renderSprite(x * 16, y * 16 + 8, 2 + 12 * 32 + 28, 2, 0);
        } else {
            screen.renderSprite(x * 16, y * 16 + 8, (l ? 20 : 21) + (d ? 2 : 1) * 32 + 11 * 32 - 3, 2, 0);
            if (sd || sl) {
                screen.renderSprite(x * 16, y * 16 + 8, (l ? 23 : 24) + (d ? 2 : 1) * 32 + 14 * 32, 2, 0);
            } else {
                screen.renderSprite(x * 16, y * 16 + 8, (l ? 20 : 21) + (d ? 2 : 1) * 32 + 14 * 32 + (isBellowOverworld ? -3 : 0), 2, 0);
            }
        }
        if (!d && !r) {
            screen.renderSprite(x * 16 + 8, y * 16 + 8, 3 + 12 * 32 + 28, 2, 0);
        } else {
            screen.renderSprite(x * 16 + 8, y * 16 + 8, (r ? 22 : 21) + (d ? 2 : 1) * 32 + 11 * 32 - 3, 2, 0);
            if (sd || sr) {
                screen.renderSprite(x * 16 + 8, y * 16 + 8, (r ? 25 : 24) + (d ? 2 : 1) * 32 + 14 * 32, 2, 0);
            } else {
                screen.renderSprite(x * 16 + 8, y * 16 + 8, (r ? 22 : 21) + (d ? 2 : 1) * 32 + 14 * 32 + (isBellowOverworld ? -3 : 0), 2, 0);
            }
        }
    }

    public boolean mayPass(Level level, int x, int y, Entity e) {
        return e.canSwim();
    }

}
