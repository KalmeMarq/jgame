package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.gfx.SpriteSheet;
import com.mojang.ld22.level.Level;

public class StoneTile extends Tile {
    public StoneTile(int id) {
        super(id);
    }

    public void render(Screen screen, Level level, int x, int y) {
        screen.renderSprite(x *  16, y * 16, SpriteSheet.uvTile(200, 16), 2, 2);
        screen.renderSprite(x *  16 + 8, y * 16, SpriteSheet.uvTile(200, 16), 2, 2);
        screen.renderSprite(x *  16, y * 16 + 8, SpriteSheet.uvTile(200, 16), 2, 2);
        screen.renderSprite(x *  16 + 8, y * 16 + 8, SpriteSheet.uvTile(200, 16), 2, 2);
    }

    public boolean mayPass(Level level, int x, int y, Entity e) {
        return false;
    }
}
