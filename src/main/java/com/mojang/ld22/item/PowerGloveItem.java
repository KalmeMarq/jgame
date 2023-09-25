package com.mojang.ld22.item;

import com.mojang.ld22.Game;
import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.Furniture;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;

public class PowerGloveItem extends Item {
    public PowerGloveItem() {
        super("Pow glove", 7 + 4 * 32, Color.get(-1, 100, 320, 430));
    }

    public void renderIcon(Screen screen, int x, int y) {
        screen.render(x, y, getSprite(), getColor(), 0);
    }

    public void renderInventory(Screen screen, int x, int y) {
        screen.render(x, y, getSprite(), getColor(), 0);
        Game.getInstance().font.draw(getName(), screen, x + 8, y, Color.get(-1, 555, 555, 555));
    }

    public boolean interact(Player player, Entity entity, int attackDir) {
        if (entity instanceof Furniture f) {
            f.take(player);
            return true;
        }
        return false;
    }
}
