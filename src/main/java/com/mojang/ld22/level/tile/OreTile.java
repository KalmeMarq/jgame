package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Mob;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.entity.particle.SmashParticle;
import com.mojang.ld22.entity.particle.TextParticle;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.Level;

public class OreTile extends Tile {
    private final Resource toDrop;

    public OreTile(int id, Resource toDrop) {
        super(id);
        this.toDrop = toDrop;
    }

    public void render(Screen screen, Level level, int x, int y) {
        screen.render(x * 16, y * 16, 17 + 32,2, 0);
        screen.render(x * 16 + 8, y * 16, 18 + 32, 2, 0);
        screen.render(x * 16, y * 16 + 8, 17 + 2 * 32, 2, 0);
        screen.render(x * 16 + 8, y * 16 + 8, 18 + 2 * 32, 2, 0);
    }

    public boolean mayPass(Level level, int x, int y, Entity e) {
        return false;
    }

    public void hurt(Level level, int x, int y, Mob source, int dmg, int attackDir) {
        hurt(level, x, y, 0);
    }

    public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem tool) {
            if (tool.type == ToolType.pickaxe) {
                if (player.payStamina(6 - tool.level)) {
                    hurt(level, xt, yt, 1);
                    return true;
                }
            }
        }
        return false;
    }

    public void hurt(Level level, int x, int y, int dmg) {
        int damage = level.getData(x, y) + 1;
        level.add(new SmashParticle(x * 16 + 8, y * 16 + 8));
        level.add(new TextParticle(String.valueOf(dmg), x * 16 + 8, y * 16 + 8, 0x9E2C2C));
        if (dmg > 0) {
            int count = this.random.nextInt(2);
            if (damage >= this.random.nextInt(10) + 3) {
                level.setTile(x, y, Tile.dirt, 0);
                count += 2;
            } else {
                level.setData(x, y, damage);
            }
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(new ResourceItem(this.toDrop), x * 16 + this.random.nextInt(10) + 3, y * 16 + this.random.nextInt(10) + 3));
            }
        }
    }

    public void bumpedInto(Level level, int x, int y, Entity entity) {
        entity.hurt(this, x, y, 3);
    }
}
