package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Mob;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.entity.particle.SmashParticle;
import com.mojang.ld22.entity.particle.TextParticle;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.gfx.SpriteSheet;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.Level;

public class RockTile extends Tile {
    public RockTile(int id) {
        super(id);
    }

    public void render(Screen screen, Level level, int x, int y) {
        boolean u = level.getTile(x, y - 1) != this;
        boolean d = level.getTile(x, y + 1) != this;
        boolean l = level.getTile(x - 1, y) != this;
        boolean r = level.getTile(x + 1, y) != this;

        boolean ul = level.getTile(x - 1, y - 1) != this;
        boolean dl = level.getTile(x - 1, y + 1) != this;
        boolean ur = level.getTile(x + 1, y - 1) != this;
        boolean dr = level.getTile(x + 1, y + 1) != this;

        boolean isBellowOverworld = level.depth < 0;

        if (!u && !l) {
            if (!ul) {
                screen.renderSprite(x * 16, y * 16, SpriteSheet.uvTile(224, 40), 2, 0);
            } else {
                screen.renderSprite(x * 16, y * 16, SpriteSheet.uvTile(184, 32), 2, 3);
            }
        } else {
            screen.renderSprite(x * 16, y * 16, SpriteSheet.uvTile(l ? 208 : 216, 136) + (isBellowOverworld ? 3 : 0) + (u ? 0 : 1) * 32, 2, 0);
            screen.renderSprite(x * 16, y * 16, (l ? 27 : 26) + (u ? 2 : 1) * 32 + 4 * 32, 2, 3);
        }

        if (!u && !r) {
            if (!ur) {
                screen.renderSprite(x * 16 + 8, y * 16, SpriteSheet.uvTile(232, 40), 2, 0);
            } else {
                screen.renderSprite(x * 16 + 8, y * 16, SpriteSheet.uvTile(192, 32), 2, 3);
            }
        } else {
            screen.renderSprite(x * 16 + 8, y * 16, SpriteSheet.uvTile(r ? 224 : 216, 136) + (isBellowOverworld ? 3 : 0) + (u ? 0 : 1) * 32, 2, 0);
            screen.renderSprite(x * 16 + 8, y * 16, (r ? 25 : 26) + (u ? 2 : 1) * 32 + 4 * 32, 2, 3);
        }

        if (!d && !l) {
            if (!dl) {
                screen.renderSprite(x * 16, y * 16 + 8, SpriteSheet.uvTile(240, 40), 2, 0);
            } else {
                screen.renderSprite(x * 16, y * 16 + 8, SpriteSheet.uvTile(184, 40), 2, 3);
            }
        } else {
            screen.renderSprite(x * 16, y * 16 + 8, SpriteSheet.uvTile(l ? 208 : 216, 136) + (isBellowOverworld ? 3 : 0) + (d ? 2 : 1) * 32, 2, 0);
            screen.renderSprite(x * 16, y * 16 + 8, (l ? 27 : 26) + (d ? 0 : 1) * 32 + 4 * 32, 2, 3);
        }
        if (!d && !r) {
            if (!dr) {
                screen.renderSprite(x * 16 + 8, y * 16 + 8, SpriteSheet.uvTile(248, 40), 2, 0);
            } else {
                screen.renderSprite(x * 16 + 8, y * 16 + 8, SpriteSheet.uvTile(192, 40), 2, 3);
            }
        } else {
            screen.renderSprite(x * 16 + 8, y * 16 + 8, SpriteSheet.uvTile(r ? 224 : 216, 136) + (isBellowOverworld ? 3 : 0) + (d ? 2 : 1) * 32, 2, 0);
            screen.renderSprite(x * 16 + 8, y * 16 + 8, (r ? 25 : 26) + (d ? 0 : 1) * 32 + 4 * 32, 2, 3);
        }
    }

    public boolean mayPass(Level level, int x, int y, Entity e) {
        return e instanceof Player;
    }

    public void hurt(Level level, int x, int y, Mob source, int dmg, int attackDir) {
        this.hurt(level, x, y, dmg);
    }

    public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem tool) {
            if (tool.type == ToolType.pickaxe) {
                if (player.payStamina(4 - tool.level)) {
                    this.hurt(level, xt, yt, this.random.nextInt(10) + (tool.level) * 5 + 10);
                    return true;
                }
            }
        }
        return false;
    }

    public void hurt(Level level, int x, int y, int dmg) {
        int damage = level.getData(x, y) + dmg;
        level.add(new SmashParticle(x * 16 + 8, y * 16 + 8));
        level.add(new TextParticle(String.valueOf(dmg), x * 16 + 8, y * 16 + 8, 0x9E2C2C));
        if (damage >= 50) {
            int count = this.random.nextInt(4) + 1;
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(new ResourceItem(Resource.stone), x * 16 + this.random.nextInt(10) + 3, y * 16 + this.random.nextInt(10) + 3));
            }
            count = this.random.nextInt(2);
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(new ResourceItem(Resource.coal), x * 16 + this.random.nextInt(10) + 3, y * 16 + this.random.nextInt(10) + 3));
            }
            level.setTile(x, y, Tile.dirt, 0);
        } else {
            level.setData(x, y, damage);
        }
    }

    public void tick(Level level, int xt, int yt) {
        int damage = level.getData(xt, yt);
        if (damage > 0) {
            level.setData(xt, yt, damage - 1);
        }
    }
}
