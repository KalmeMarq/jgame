package com.mojang.ld22.entity;

import com.mojang.ld22.crafting.Crafting;
import com.mojang.ld22.screen.CraftingMenu;

public class Workbench extends Furniture {
    public Workbench() {
        super("minicraft.workbench", 7 * 32 + 9);
        this.sprite = 24 * 32 + 28;
        this.xr = 3;
        this.yr = 2;
    }

    public boolean use(Player player, int attackDir) {
        player.game.setMenu(new CraftingMenu(Crafting.INSTANCE.workbenchRecipes, player));
        return true;
    }
}
