package com.mojang.ld22.entity;

import com.mojang.ld22.crafting.Crafting;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.screen.CraftingMenu;

public class Workbench extends Furniture {
    public Workbench() {
        super("Workbench", 7 * 32 + 9);
        this.col = Color.get(-1, 100, 321, 431);
        this.sprite = 4;
        this.xr = 3;
        this.yr = 2;
    }

    public boolean use(Player player, int attackDir) {
        player.game.setMenu(new CraftingMenu(Crafting.INSTANCE.workbenchRecipes, player));
        return true;
    }
}
