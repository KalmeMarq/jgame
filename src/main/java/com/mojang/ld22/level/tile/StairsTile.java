package com.mojang.ld22.level.tile;

import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.level.Level;

public class StairsTile extends Tile {
    private final boolean leadsUp;

    public StairsTile(int id, boolean leadsUp) {
        super(id);
        this.leadsUp = leadsUp;
    }

    public void render(Screen screen, Level level, int x, int y) {
        int xt = 0;
        if (this.leadsUp) {
            xt = 2;
        }

        if (level.depth <= 0) {
            Tile.dirt.render(screen, level, x, y);
        } else {
            Tile.cloud.render(screen, level, x, y);
        }

        screen.renderSprite(x * 16, y * 16, 24 + xt, 2, 0);
        screen.renderSprite(x * 16 + 8, y * 16, 24 + xt + 1, 2, 0);
        screen.renderSprite(x * 16, y * 16 + 8, 24 + xt + 32, 2, 0);
        screen.renderSprite(x * 16 + 8, y * 16 + 8, 24 + xt + 1 + 32, 2, 0);
    }
}
