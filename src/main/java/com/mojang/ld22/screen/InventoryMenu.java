package com.mojang.ld22.screen;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Screen;

public class InventoryMenu extends Menu {
    private final Player player;
    private int selected = 0;

    public InventoryMenu(Player player) {
        this.player = player;

        if (player.activeItem != null) {
            player.inventory.items.add(0, player.activeItem);
            player.activeItem = null;
        }
    }

    public void tick() {
        if (this.input.menu.clicked) {
            this.game.setMenu(null);
        }

        if (this.input.up.clicked) {
            this.selected--;
        }
        if (this.input.down.clicked) {
            this.selected++;
        }

        int len = this.player.inventory.items.size();
        if (len == 0) {
            this.selected = 0;
        }
        if (this.selected < 0) {
            this.selected += len;
        }
        if (this.selected >= len) {
            this.selected -= len;
        }

        if (this.input.attack.clicked && len > 0) {
            this.player.activeItem = this.player.inventory.items.remove(this.selected);
            this.game.setMenu(null);
        }
    }

    public void render(Screen screen) {
        this.font.renderFrame(screen, "Inventory", 1, 1, 12, 11);
        renderItemList(screen, 1, 1, 12, 11, this.player.inventory.items, this.selected);
    }
}
