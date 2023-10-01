package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.AirWizard;
import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.Mob;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.entity.particle.SmashParticle;
import com.mojang.ld22.entity.particle.TextParticle;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.level.Level;

public class CloudCactusTile extends Tile {
    public CloudCactusTile(int id) {
        super(id);
    }

    public void render(Screen screen, Level level, int x, int y) {
        Tile.cloud.render(screen, level, x, y);
        screen.renderTextured(x * 16, y * 16, 16, 16, 160, 16, 2, 0xFFFFFF, 0);
    }

    public boolean mayPass(Level level, int x, int y, Entity e) {
        return e instanceof AirWizard;
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
            if (damage >= 10) {
                level.setTile(x, y, Tile.cloud, 0);
            } else {
                level.setData(x, y, damage);
            }
        }
    }

    public void bumpedInto(Level level, int x, int y, Entity entity) {
        if (entity instanceof AirWizard) {
            return;
        }
        entity.hurt(this, x, y, 3);
    }
}
