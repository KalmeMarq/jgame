package com.mojang.ld22.entity;

import com.mojang.ld22.crafting.Crafting;
import com.mojang.ld22.screen.CraftingMenu;

public class Anvil extends Furniture {
    public Anvil() {
        super("minicraft.anvil", 7 * 32 + 5);
        this.sprite = 24 * 32 + 24;
        this.xr = 3;
        this.yr = 2;
    }

    public boolean use(Player player, int attackDir) {
        player.game.setMenu(new CraftingMenu(Crafting.INSTANCE.anvilRecipes, player));
        return true;
    }
}
