package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Mob;
import com.mojang.ld22.entity.particle.SmashParticle;
import com.mojang.ld22.entity.particle.TextParticle;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.Level;

public class CactusTile extends Tile {
    public CactusTile(int id) {
        super(id);
        this.connectsToSand = true;
    }

    public void render(Screen screen, Level level, int x, int y) {
        Tile.sand.render(screen, level, x, y);
        screen.renderSprite(x * 16, y * 16, 28, 2, 0);
        screen.renderSprite(x * 16 + 8, y * 16, 29, 2, 0);
        screen.renderSprite(x * 16, y * 16 + 8, 28 + 1 * 32, 2, 0);
        screen.renderSprite(x * 16 + 8, y * 16 + 8, 29 + 1 * 32, 2, 0);
    }

    public boolean mayPass(Level level, int x, int y, Entity e) {
        return false;
    }

    public void hurt(Level level, int x, int y, Mob source, int dmg, int attackDir) {
        int damage = level.getData(x, y) + dmg;
        level.add(new SmashParticle(x * 16 + 8, y * 16 + 8));
        level.add(new TextParticle(String.valueOf(dmg), x * 16 + 8, y * 16 + 8, 0x9E2C2C));
        if (damage >= 10) {
            int count = this.random.nextInt(2) + 1;
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(new ResourceItem(Resource.cactusFlower), x * 16 + this.random.nextInt(10) + 3, y * 16 + this.random.nextInt(10) + 3));
            }
            level.setTile(x, y, Tile.sand, 0);
        } else {
            level.setData(x, y, damage);
        }
    }

    public void bumpedInto(Level level, int x, int y, Entity entity) {
        entity.hurt(this, x, y, 1);
    }

    public void tick(Level level, int xt, int yt) {
        int damage = level.getData(xt, yt);
        if (damage > 0) {
            level.setData(xt, yt, damage - 1);
        }
    }
}
