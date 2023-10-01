package com.mojang.ld22.item;

import com.mojang.ld22.Game;
import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.Furniture;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Screen;

public class PowerGloveItem extends Item {
    public PowerGloveItem() {
        super("Pow glove", 7 + 6 * 32);
    }

    public void renderIcon(Screen screen, int x, int y) {
        screen.renderSprite(x, y, this.getSprite(), 2, 0);
    }

    public void renderInventory(Screen screen, int x, int y) {
        screen.renderSprite(x, y, this.getSprite(), 2, 0);
        Game.getInstance().font.draw(this.getName(), screen, x + 8, y, 0xFFFFFF);
    }

    public boolean interact(Player player, Entity entity, int attackDir) {
        if (entity instanceof Furniture furniture) {
            furniture.take(player);
            return true;
        }
        return false;
    }
}
