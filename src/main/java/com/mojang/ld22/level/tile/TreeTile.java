package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Mob;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.entity.particle.SmashParticle;
import com.mojang.ld22.entity.particle.TextParticle;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.Level;

public class TreeTile extends Tile {
    public TreeTile(int id) {
        super(id);
        this.connectsToGrass = true;
    }

    public void render(Screen screen, Level level, int x, int y) {
        boolean u = level.getTile(x, y - 1) == this;
        boolean l = level.getTile(x - 1, y) == this;
        boolean r = level.getTile(x + 1, y) == this;
        boolean d = level.getTile(x, y + 1) == this;
        boolean ul = level.getTile(x - 1, y - 1) == this;
        boolean ur = level.getTile(x + 1, y - 1) == this;
        boolean dl = level.getTile(x - 1, y + 1) == this;
        boolean dr = level.getTile(x + 1, y + 1) == this;

        Tile.grass.render(screen, level, x, y);

        if (u && ul && l) {
            screen.renderSprite(x * 16, y * 16, 10 + 32 + 21, 2, 0);
        } else {
            screen.renderSprite(x * 16, y * 16, 9 + 21, 2, 0);
        }
        if (u && ur && r) {
            screen.renderSprite(x * 16 + 8, y * 16, 10 + 2 * 32 + 21, 2, 0);
        } else {
            screen.renderSprite(x * 16 + 8, y * 16, 10 + 21, 2, 0);
        }
        if (d && dl && l) {
            screen.renderSprite(x * 16, y * 16 + 8, 10 + 2 * 32 + 21, 2, 0);
        } else {
            screen.renderSprite(x * 16, y * 16 + 8, 9 + 32 + 21, 2, 0);
        }
        if (d && dr && r) {
            screen.renderSprite(x * 16 + 8, y * 16 + 8, 10 + 32 + 21, 2, 0);
        } else {
            screen.renderSprite(x * 16 + 8, y * 16 + 8, 10 + 3 * 32 + 21, 2, 0);
        }
    }

    public void tick(Level level, int xt, int yt) {
        int damage = level.getData(xt, yt);
        if (damage > 0) {
            level.setData(xt, yt, damage - 1);
        }
    }

    public boolean mayPass(Level level, int x, int y, Entity e) {
        return e instanceof Player;
    }

    public void hurt(Level level, int x, int y, Mob source, int dmg, int attackDir) {
        hurt(level, x, y, dmg);
    }

    public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem tool) {
            if (tool.type == ToolType.axe) {
                if (player.payStamina(4 - tool.level)) {
                    hurt(level, xt, yt, this.random.nextInt(10) + (tool.level) * 5 + 10);
                    return true;
                }
            }
        }
        return false;
    }

    private void hurt(Level level, int x, int y, int dmg) {
        {
            int count = this.random.nextInt(10) == 0 ? 1 : 0;
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(new ResourceItem(Resource.apple), x * 16 + this.random.nextInt(10) + 3, y * 16 + this.random.nextInt(10) + 3));
            }
        }
        int damage = level.getData(x, y) + dmg;
        level.add(new SmashParticle(x * 16 + 8, y * 16 + 8));
        level.add(new TextParticle(String.valueOf(dmg), x * 16 + 8, y * 16 + 8, 0x9E2C2C));
        if (damage >= 20) {
            int count = this.random.nextInt(2) + 1;
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(new ResourceItem(Resource.wood), x * 16 + this.random.nextInt(10) + 3, y * 16 + this.random.nextInt(10) + 3));
            }
            count = this.random.nextInt(this.random.nextInt(4) + 1);
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(new ResourceItem(Resource.acorn), x * 16 + this.random.nextInt(10) + 3, y * 16 + this.random.nextInt(10) + 3));
            }
            level.setTile(x, y, Tile.grass, 0);
        } else {
            level.setData(x, y, damage);
        }
    }
}
