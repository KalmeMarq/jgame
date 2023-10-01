package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.level.Level;

public class FarmTile extends Tile {
    public FarmTile(int id) {
        super(id);
    }

    public void render(Screen screen, Level level, int x, int y) {
        screen.renderSprite(x * 16, y * 16, 30 + 3 * 32, 2, 1);
        screen.renderSprite(x * 16 + 8, y * 16, 30 + 3 * 32, 2, 0);
        screen.renderSprite(x * 16, y * 16 + 8, 30 + 3 * 32, 2, 0);
        screen.renderSprite(x * 16 + 8, y * 16 + 8, 30 + 3 * 32, 2, 1);
    }

    public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem tool) {
            if (tool.type == ToolType.shovel) {
                if (player.payStamina(4 - tool.level)) {
                    level.setTile(xt, yt, Tile.dirt, 0);
                    return true;
                }
            }
        }
        return false;
    }

    public void tick(Level level, int xt, int yt) {
        int age = level.getData(xt, yt);
        if (age < 5) {
            level.setData(xt, yt, age + 1);
        }
    }

    public void steppedOn(Level level, int xt, int yt, Entity entity) {
        if (this.random.nextInt(60) != 0) {
            return;
        }
        if (level.getData(xt, yt) < 5) {
            return;
        }
        level.setTile(xt, yt, Tile.dirt, 0);
    }
}
