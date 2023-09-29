package com.mojang.ld22.entity;

import com.mojang.ld22.crafting.Crafting;
import com.mojang.ld22.screen.CraftingMenu;

public class Furnace extends Furniture {
    public Furnace() {
        super("Furnace", 7 * 32 + 8);
        this.sprite = 24 * 32 + 27;
        this.xr = 3;
        this.yr = 2;
    }

    public boolean use(Player player, int attackDir) {
        player.game.setMenu(new CraftingMenu(Crafting.INSTANCE.furnaceRecipes, player));
        return true;
    }
}
