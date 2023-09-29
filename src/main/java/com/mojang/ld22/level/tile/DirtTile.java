package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.sound.Sound;

public class DirtTile extends Tile {
    public DirtTile(int id) {
        super(id);
    }

    public void render(Screen screen, Level level, int x, int y) {
        screen.renderSprite(x * 16, y * 16, 7 * 32 + 28, 2, 0);
        screen.renderSprite(x * 16 + 8, y * 16, 1 + 7 * 32 + 28, 2, 0);
        screen.renderSprite(x * 16, y * 16 + 8, 2 + 7 * 32 + 28, 2, 0);
        screen.renderSprite(x * 16 + 8, y * 16 + 8, 3 + 7 * 32 + 28, 2, 0);
    }

    public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem tool) {
            if (tool.type == ToolType.shovel) {
                if (player.payStamina(4 - tool.level)) {
                    level.setTile(xt, yt, Tile.hole, 0);
                    level.add(new ItemEntity(new ResourceItem(Resource.dirt), xt * 16 + this.random.nextInt(10) + 3, yt * 16 + this.random.nextInt(10) + 3));
                    Sound.play(Sound.Event.MONSTER_HURT, 1.0f);
                    return true;
                }
            }
            if (tool.type == ToolType.hoe) {
                if (player.payStamina(4 - tool.level)) {
                    level.setTile(xt, yt, Tile.farmland, 0);
                    Sound.play(Sound.Event.MONSTER_HURT, 1.0f);
                    return true;
                }
            }
        }
        return false;
    }
}
