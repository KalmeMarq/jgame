package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.gfx.SpriteSheet;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.Level;

public class CloudTile extends Tile {
    public CloudTile(int id) {
        super(id);
    }

    public void render(Screen screen, Level level, int x, int y) {
        boolean u = level.getTile(x, y - 1) == Tile.infiniteFall;
        boolean d = level.getTile(x, y + 1) == Tile.infiniteFall;
        boolean l = level.getTile(x - 1, y) == Tile.infiniteFall;
        boolean r = level.getTile(x + 1, y) == Tile.infiniteFall;

        boolean ul = level.getTile(x - 1, y - 1) == Tile.infiniteFall;
        boolean dl = level.getTile(x - 1, y + 1) == Tile.infiniteFall;
        boolean ur = level.getTile(x + 1, y - 1) == Tile.infiniteFall;
        boolean dr = level.getTile(x + 1, y + 1) == Tile.infiniteFall;

        if (!u && !l) {
            if (!ul) {
                screen.renderSprite(x * 16, y * 16, SpriteSheet.uvTile(192, 56), 2, 0);
            } else {
                screen.renderSprite(x * 16, y * 16, SpriteSheet.uvTile(120, 0), 2, 3);
            }
        } else {
            screen.renderSprite(x * 16, y * 16, (l ? 19 : 18) + (u ? 2 : 1) * 32, 2, 3);
        }

        if (!u && !r) {
            if (!ur) {
                screen.renderSprite(x * 16 + 8, y * 16, SpriteSheet.uvTile(200, 56), 2, 0);
            } else {
                screen.renderSprite(x * 16 + 8, y * 16, SpriteSheet.uvTile(128, 0), 2, 3);
            }
        } else {
            screen.renderSprite(x * 16 + 8, y * 16, (r ? 17 : 18) + (u ? 2 : 1) * 32, 2, 3);
        }

        if (!d && !l) {
            if (!dl) {
                screen.renderSprite(x * 16, y * 16 + 8, SpriteSheet.uvTile(208, 56), 2, 0);
            } else {
                screen.renderSprite(x * 16, y * 16 + 8, SpriteSheet.uvTile(120, 8), 2, 3);
            }
        } else {
            screen.renderSprite(x * 16, y * 16 + 8, (l ? 19 : 18) + (d ? 0 : 1) * 32, 2, 3);
        }
        if (!d && !r) {
            if (!dr) {
                screen.renderSprite(x * 16 + 8, y * 16 + 8, SpriteSheet.uvTile(216, 56), 2, 0);
            } else {
                screen.renderSprite(x * 16 + 8, y * 16 + 8, SpriteSheet.uvTile(128, 8), 2, 3);
            }
        } else {
            screen.renderSprite(x * 16 + 8, y * 16 + 8, (r ? 17 : 18) + (d ? 0 : 1) * 32, 2, 3);
        }
    }

    public boolean mayPass(Level level, int x, int y, Entity e) {
        return true;
    }

    public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem tool) {
            if (tool.type == ToolType.shovel) {
                if (player.payStamina(5)) {
                    // level.setTile(xt, yt, Tile.infiniteFall, 0);
                    int count = this.random.nextInt(2) + 1;
                    for (int i = 0;
                         i < count;
                         i++) {
                        level.add(new ItemEntity(new ResourceItem(Resource.cloud), xt * 16 + this.random.nextInt(10) + 3, yt * 16 + this.random.nextInt(10) + 3));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) { if (item instanceof ToolItem) { ToolItem tool = (ToolItem) item; if (tool.type == ToolType.pickaxe) { if (player.payStamina(4 - tool.level)) { hurt(level, xt, yt, random.nextInt(10) + (tool.level) * 5 + 10); return true; } } } return false; }
     */
}
