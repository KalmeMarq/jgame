package com.mojang.ld22.entity;

import com.mojang.ld22.crafting.Crafting;
import com.mojang.ld22.screen.CraftingMenu;

public class Oven extends Furniture {
    public Oven() {
        super("minicraft.oven", 7 * 32 + 7);
        this.sprite = 24 * 32 + 26;
        this.xr = 3;
        this.yr = 2;
    }

    public boolean use(Player player, int attackDir) {
        player.game.setMenu(new CraftingMenu(Crafting.INSTANCE.ovenRecipes, player));
        return true;
    }
}
