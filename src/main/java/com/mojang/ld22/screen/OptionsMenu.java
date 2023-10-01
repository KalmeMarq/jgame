package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Screen;

public class OptionsMenu extends Menu {
    private final Menu parentMenu;

    public OptionsMenu(Menu parentMenu) {
        this.parentMenu = parentMenu;
    }

    @Override
    public void tick() {
        if (this.input.attack.clicked || this.input.menu.clicked) {
            this.game.setMenu(this.parentMenu);
        }
        super.tick();
    }

    @Override
    public void render(Screen screen) {
        screen.clear(0);
    }
}
