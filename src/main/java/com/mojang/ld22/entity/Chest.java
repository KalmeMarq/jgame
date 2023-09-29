package com.mojang.ld22.entity;

import com.mojang.ld22.screen.ContainerMenu;

public class Chest extends Furniture {
    public Inventory inventory = new Inventory();

    public Chest() {
        super("Chest", 7 * 32 + 6);
        this.sprite = 24 * 32 + 22;
    }

    public boolean use(Player player, int attackDir) {
        player.game.setMenu(new ContainerMenu(player, "Chest", this.inventory));
        return true;
    }
}
