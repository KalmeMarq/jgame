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

public class HardRockTile extends Tile {
    public HardRockTile(int id) {
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

        if (!u && !l) {
            if (!ul) {
                screen.renderSprite(x * 16, y * 16, SpriteSheet.uvTile(224, 88), 2, 0);
            } else {
                screen.renderSprite(x * 16, y * 16, SpriteSheet.uvTile(144, 32), 2, 3);
            }
        } else {
            screen.renderSprite(x * 16, y * 16, SpriteSheet.uvTile(l ? 208 : 216, 136) + (u ? 0 : 1) * 32, 2, 0);
            screen.renderSprite(x * 16, y * 16, (l ? 22 : 21) + (u ? 2 : 1) * 32 + 4 * 32, 2, 3);
        }

        if (!u && !r) {
            if (!ur) {
                screen.renderSprite(x * 16 + 8, y * 16, SpriteSheet.uvTile(232, 48), 2, 0);
            } else {
                screen.renderSprite(x * 16 + 8, y * 16, SpriteSheet.uvTile(152, 32), 2, 3);
            }
        } else {
            screen.renderSprite(x * 16 + 8, y * 16, SpriteSheet.uvTile(r ? 224 : 216, 136) + (u ? 0 : 1) * 32, 2, 0);
            screen.renderSprite(x * 16 + 8, y * 16, (r ? 20 : 21) + (u ? 2 : 1) * 32 + 4 * 32, 2, 3);
        }

        if (!d && !l) {
            if (!dl) {
                screen.renderSprite(x * 16, y * 16 + 8, SpriteSheet.uvTile(240, 88), 2, 0);
            } else {
                screen.renderSprite(x * 16, y * 16 + 8, SpriteSheet.uvTile(144, 40), 2, 3);
            }
        } else {
            screen.renderSprite(x * 16, y * 16 + 8, SpriteSheet.uvTile(l ? 208 : 216, 136) + (d ? 2 : 1) * 32, 2, 0);
            screen.renderSprite(x * 16, y * 16 + 8, (l ? 22 : 21) + (d ? 0 : 1) * 32 + 4 * 32, 2, 3);
        }
        if (!d && !r) {
            if (!dr) {
                screen.renderSprite(x * 16 + 8, y * 16 + 8, SpriteSheet.uvTile(248, 88), 2, 0);
            } else {
                screen.renderSprite(x * 16 + 8, y * 16 + 8, SpriteSheet.uvTile(152, 40), 2, 3);
            }
        } else {
            screen.renderSprite(x * 16 + 8, y * 16 + 8, SpriteSheet.uvTile(r ? 224 : 216, 136) + (d ? 2 : 1) * 32, 2, 0);
            screen.renderSprite(x * 16 + 8, y * 16 + 8, (r ? 20 : 21) + (d ? 0 : 1) * 32 + 4 * 32, 2, 3);
        }
    }

    public boolean mayPass(Level level, int x, int y, Entity e) {
        return e instanceof Player;
    }

    public void hurt(Level level, int x, int y, Mob source, int dmg, int attackDir) {
        hurt(level, x, y, 0);
    }

    public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
        if (item instanceof ToolItem tool) {
            if (tool.type == ToolType.pickaxe && tool.level == 4) {
                if (player.payStamina(4 - tool.level)) {
                    hurt(level, xt, yt, this.random.nextInt(10) + (tool.level) * 5 + 10);
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
        if (damage >= 200) {
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
