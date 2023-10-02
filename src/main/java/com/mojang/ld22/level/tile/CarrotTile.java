package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Mob;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.Level;

public class CarrotTile extends Tile {
    public CarrotTile(int id) {
        super(id);
    }

    public void render(Screen screen, Level level, int x, int y) {
        Tile.farmland.render(screen, level, x, y);

        int age = level.getData(x, y);
        int icon = age / 10;
        int xt = 27;
        int yt = 15 * 32;

        if (icon == 1) {
            xt++;
        } else if (icon == 2) {
            xt += 2;
        } else if (icon == 3) {
            xt += 3;
        } else if (icon == 4) {
            yt += 32;
        } else if (icon == 5) {
            xt += 1;
            yt += 32;
        }

        screen.renderSprite(x * 16, y * 16, xt + yt, 2, 0);
        screen.renderSprite(x * 16 + 8, y * 16, xt + yt, 2, 0);
        screen.renderSprite(x * 16, y * 16 + 8, xt + yt, 2, 1);
        screen.renderSprite(x * 16 + 8, y * 16 + 8, xt + yt, 2, 1);
    }

    public void tick(Level level, int xt, int yt) {
        if (this.random.nextInt(2) == 0) {
            return;
        }

        int age = level.getData(xt, yt);
        if (age < 50) {
            level.setData(xt, yt, age + 1);
        }
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

    public void steppedOn(Level level, int xt, int yt, Entity entity) {
        if (this.random.nextInt(60) != 0) {
            return;
        }
        if (level.getData(xt, yt) < 2) {
            return;
        }
        harvest(level, xt, yt);
    }

    public void hurt(Level level, int x, int y, Mob source, int dmg, int attackDir) {
        harvest(level, x, y);
    }

    private void harvest(Level level, int x, int y) {
        int age = level.getData(x, y);

        int count = 0;
        if (age == 50) {
            count = this.random.nextInt(3) + 2;
        } else if (age >= 40) {
            count = this.random.nextInt(2) + 1;
        }
        for (int i = 0; i < count; i++) {
            level.add(new ItemEntity(new ResourceItem(Resource.carrot), x * 16 + this.random.nextInt(10) + 3, y * 16 + this.random.nextInt(10) + 3));
        }

        level.setTile(x, y, Tile.dirt, 0);
    }
}
