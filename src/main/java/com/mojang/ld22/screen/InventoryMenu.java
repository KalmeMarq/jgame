package com.mojang.ld22.screen;

import com.mojang.ld22.Language;
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

        int inventorySize = this.player.inventory.items.size();
        if (inventorySize > 0) {
            if (this.input.up.clicked) {
                this.selected = Math.floorMod(this.selected - 1, inventorySize);
            }
            if (this.input.down.clicked) {
                this.selected = Math.floorMod(this.selected + 1, inventorySize);
            }

            if (this.input.attack.clicked) {
                this.player.activeItem = this.player.inventory.items.remove(this.selected);
                this.game.setMenu(null);
            }
        }
    }

    public void render(Screen screen) {
        this.font.renderFrame(screen, Language.translate("inventory.menu.title"), 1, 1, 12, 11);
        this.renderItemList(screen, 1, 1, 12, 11, this.player.inventory.items, this.selected);
    }
}
