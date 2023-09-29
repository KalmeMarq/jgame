package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;

public class InstructionsMenu extends Menu {
    private final Menu parent;

    public InstructionsMenu(Menu parent) {
        this.parent = parent;
    }

    public void tick() {
        if (this.input.attack.clicked || this.input.menu.clicked) {
            this.game.setMenu(this.parent);
        }
    }

    public void render(Screen screen) {
        screen.clear(0);

        this.font.draw("HOW TO PLAY", screen, 4 * 8 + 4, 8, 0xFFFFFF);
        this.font.draw("Move your character", screen, 4, 3 * 8, 0xC8C8C8);
        this.font.draw("with the arrow keys", screen, 4, 4 * 8, 0xC8C8C8);
        this.font.draw("press C to attack", screen, 4, 5 * 8, 0xC8C8C8);
        this.font.draw("and X to open the", screen, 4, 6 * 8, 0xC8C8C8);
        this.font.draw("inventory and to", screen, 4, 7 * 8, 0xC8C8C8);
        this.font.draw("use items.", screen, 4, 8 * 8, 0xC8C8C8);
        this.font.draw("Select an item in", screen, 4, 9 * 8, 0xC8C8C8);
        this.font.draw("the inventory to", screen, 4, 10 * 8, 0xC8C8C8);
        this.font.draw("equip it.", screen, 4, 11 * 8, 0xC8C8C8);
        this.font.draw("Kill the air wizard", screen, 4, 12 * 8, 0xC8C8C8);
        this.font.draw("to win the game!", screen, 4, 13 * 8, 0xC8C8C8);
    }
}
