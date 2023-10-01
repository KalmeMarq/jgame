package com.mojang.ld22.level.tile;

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
import com.mojang.ld22.sound.Sound;

public class GrassTile extends Tile {
    public GrassTile(int id) {
        super(id);
        this.connectsToGrass = true;
    }

    public void render(Screen screen, Level level, int x, int y) {
        boolean u = !level.getTile(x, y - 1).connectsToGrass;
        boolean d = !level.getTile(x, y + 1).connectsToGrass;
        boolean l = !level.getTile(x - 1, y).connectsToGrass;
        boolean r = !level.getTile(x + 1, y).connectsToGrass;

        if (!u && !l) {
            screen.renderSprite(x * 16, y * 16, 4 * 32 + 28, 2, 0);
        } else {
            screen.renderSprite(x * 16, y * 16, SpriteSheet.uvTile(l ? 160 : 168, 112) + (u ? 0 : 1) * 32, 2, 0);
            screen.renderSprite(x * 16, y * 16, SpriteSheet.uvTile(l ? 160 : 168, 64) + (u ? 0 : 1) * 32, 2, 0);
        }

        if (!u && !r) {
            screen.renderSprite(x * 16 + 8, y * 16, 1 + 4 * 32 + 28, 2, 0);
        } else {
            screen.renderSprite(x * 16 + 8, y * 16, SpriteSheet.uvTile(r ? 176 : 168, 112) + (u ? 0 : 1) * 32, 2, 0);
            screen.renderSprite(x * 16 + 8, y * 16, SpriteSheet.uvTile(r ? 176 : 168, 64) + (u ? 0 : 1) * 32, 2, 0);
        }

        if (!d && !l) {
            screen.renderSprite(x * 16, y * 16 + 8, 2 + 4 * 32 + 28, 2, 0);
        } else {
            screen.renderSprite(x * 16, y * 16 + 8, SpriteSheet.uvTile(l ? 160 : 168, 112) + (d ? 2 : 1) * 32, 2, 0);
            screen.renderSprite(x * 16, y * 16 + 8, SpriteSheet.uvTile(l ? 160 : 168, 64) + (d ? 2 : 1) * 32, 2, 0);
        }
        if (!d && !r) {
            screen.renderSprite(x * 16 + 8, y * 16 + 8, 3 + 4 * 32 + 28, 2, 0);
        } else {
            screen.renderSprite(x * 16 + 8, y * 16 + 8, SpriteSheet.uvTile(r ? 176 : 168, 112) + (d ? 2 : 1) * 32, 2, 0);
            screen.renderSprite(x * 16 + 8, y * 16 + 8, SpriteSheet.uvTile(r ? 176 : 168, 64) + (d ? 2 : 1) * 32, 2, 0);
        }
    }

    public void tick(Level level, int xt, int yt) {
        if (this.random.nextInt(40) != 0) {
            return;
        }

        int xn = xt;
        int yn = yt;

        if (this.random.nextBoolean()) {
            xn += this.random.nextInt(2) * 2 - 1;
        } else {
            yn += this.random.nextInt(2) * 2 - 1;
        }

        if (level.getTile(xn, yn) == Tile.dirt) {
            level.setTile(xn, yn, this, 0);
        }
    }

    public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem tool) {
            if (tool.type == ToolType.shovel) {
                if (player.payStamina(4 - tool.level)) {
                    level.setTile(xt, yt, Tile.dirt, 0);
                    Sound.play(Sound.Event.MONSTER_HURT, 1.0f);
                    if (this.random.nextInt(5) == 0) {
                        level.add(new ItemEntity(new ResourceItem(Resource.seeds), xt * 16 + this.random.nextInt(10) + 3, yt * 16 + this.random.nextInt(10) + 3));
                        return true;
                    }
                }
            }
            if (tool.type == ToolType.hoe) {
                if (player.payStamina(4 - tool.level)) {
                    Sound.play(Sound.Event.MONSTER_HURT, 1.0f);
                    if (this.random.nextInt(5) == 0) {
                        level.add(new ItemEntity(new ResourceItem(Resource.seeds), xt * 16 + this.random.nextInt(10) + 3, yt * 16 + this.random.nextInt(10) + 3));
                        return true;
                    }
                    level.setTile(xt, yt, Tile.farmland, 0);
                    return true;
                }
            }
        }
        return false;

    }
}
