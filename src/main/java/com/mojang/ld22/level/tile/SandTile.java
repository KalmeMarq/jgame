package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Mob;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.gfx.SpriteSheet;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.Level;

public class SandTile extends Tile {
    public SandTile(int id) {
        super(id);
        this.connectsToSand = true;
    }

    public void render(Screen screen, Level level, int x, int y) {
        boolean u = !level.getTile(x, y - 1).connectsToSand;
        boolean d = !level.getTile(x, y + 1).connectsToSand;
        boolean l = !level.getTile(x - 1, y).connectsToSand;
        boolean r = !level.getTile(x + 1, y).connectsToSand;

        boolean steppedOn = level.getData(x, y) > 0;

        if (!u && !l) {
            if (!steppedOn) {
                screen.renderSprite(x * 16, y * 16, 10 * 32 + 28, 2, 0);
            } else {
                screen.renderSprite(x * 16, y * 16, 26 + 2 * 32, 2, 0);
            }
        } else {
            screen.renderSprite(x * 16, y * 16, SpriteSheet.uvTile(l ? 160 : 168, 112) + (u ? 0 : 1) * 32, 2, 0);
            screen.renderSprite(x * 16, y * 16, SpriteSheet.uvTile(l ? 184 : 192, 64) + (u ? 0 : 1) * 32, 2, 0);

//            screen.render(x * 16, y * 16, (l ? 11 : 12) + (u ? 0 : 1) * 32, transitionColor, 0);
        }

        if (!u && !r) {
            screen.renderSprite(x * 16 + 8, y * 16, 1 + 10 * 32 + 28, 2, 0);
        } else {
            screen.renderSprite(x * 16 + 8, y * 16, SpriteSheet.uvTile(r ? 176 : 168, 112) + (u ? 0 : 1) * 32, 2, 0);
            screen.renderSprite(x * 16 + 8, y * 16, SpriteSheet.uvTile(r ? 200 : 192, 64) + (u ? 0 : 1) * 32, 2, 0);
//            screen.render(x * 16 + 8, y * 16, (r ? 13 : 12) + (u ? 0 : 1) * 32, transitionColor, 0);
        }

        if (!d && !l) {
            screen.renderSprite(x * 16, y * 16 + 8, 2 + 10 * 32 + 28, 2, 0);
        } else {
//            screen.render(x * 16, y * 16 + 8, (l ? 11 : 12) + (d ? 2 : 1) * 32, transitionColor, 0);
            screen.renderSprite(x * 16, y * 16 + 8, SpriteSheet.uvTile(l ? 160 : 168, 112) + (d ? 2 : 1) * 32, 2, 0);
            screen.renderSprite(x * 16, y * 16 + 8, SpriteSheet.uvTile(l ? 184 : 192, 64) + (d ? 2 : 1) * 32, 2, 0);
        }
        if (!d && !r) {
            if (!steppedOn) {
                screen.renderSprite(x * 16 + 8, y * 16 + 8, 3 + 10 * 32 + 28, 2, 0);
            } else {
                screen.renderSprite(x * 16 + 8, y * 16 + 8, 26 + 2 * 32, 2, 0);
            }

        } else {
//            screen.render(x * 16 + 8, y * 16 + 8, (r ? 13 : 12) + (d ? 2 : 1) * 32, transitionColor, 0);
            screen.renderSprite(x * 16 + 8, y * 16 + 8, SpriteSheet.uvTile(r ? 176 : 168, 112) + (d ? 2 : 1) * 32, 2, 0);
            screen.renderSprite(x * 16 + 8, y * 16 + 8, SpriteSheet.uvTile(r ? 200 : 192, 64) + (d ? 2 : 1) * 32, 2, 0);
        }
    }

    public void tick(Level level, int x, int y) {
        int d = level.getData(x, y);
        if (d > 0) {
            level.setData(x, y, d - 1);
        }
    }

    public void steppedOn(Level level, int x, int y, Entity entity) {
        if (entity instanceof Mob) {
            level.setData(x, y, 10);
        }
    }

    public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem tool) {
            if (tool.type == ToolType.shovel) {
                if (player.payStamina(4 - tool.level)) {
                    level.setTile(xt, yt, Tile.dirt, 0);
                    level.add(new ItemEntity(new ResourceItem(Resource.sand), xt * 16 + this.random.nextInt(10) + 3, yt * 16 + this.random.nextInt(10) + 3));
                    return true;
                }
            }
        }
        return false;
    }
}
