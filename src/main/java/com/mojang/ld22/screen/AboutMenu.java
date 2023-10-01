package com.mojang.ld22.screen;

import com.mojang.ld22.Language;
import com.mojang.ld22.gfx.Screen;

public class AboutMenu extends Menu {
    private final Menu parent;

    public AboutMenu(Menu parent) {
        this.parent = parent;
    }

    public void tick() {
        if (this.input.attack.clicked || this.input.menu.clicked) {
            this.game.setMenu(this.parent);
        }
    }

    public void render(Screen screen) {
        screen.clear(0);

        this.font.draw(Language.translate("about.menu.title"), screen, 2 * 8 + 4, 8, 0xFFFFFF);
        this.font.draw(Language.translate("about.menu.line0"), screen, 4, 3 * 8, 0x949494);
        this.font.draw(Language.translate("about.menu.line1"), screen, 4, 4 * 8, 0x949494);
        this.font.draw(Language.translate("about.menu.line2"), screen, 4, 5 * 8, 0x949494);
        this.font.draw(Language.translate("about.menu.line3"), screen, 4, 6 * 8, 0x949494);
        this.font.draw(Language.translate("about.menu.line4"), screen, 4, 7 * 8, 0x949494);
        this.font.draw(Language.translate("about.menu.line5"), screen, 4, 9 * 8, 0x949494);
        this.font.draw(Language.translate("about.menu.line6"), screen, 4, 10 * 8, 0x949494);
    }
}
