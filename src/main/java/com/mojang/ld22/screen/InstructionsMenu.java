package com.mojang.ld22.screen;

import com.mojang.ld22.Language;
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

        this.font.draw(Language.translate("how_to_play.menu.title"), screen, 4 * 8 + 4, 8, 0xFFFFFF);
        this.font.draw(Language.translate("how_to_play.menu.line0"), screen, 4, 3 * 8, 0x949494);
        this.font.draw(Language.translate("how_to_play.menu.line1"), screen, 4, 4 * 8, 0x949494);
        this.font.draw(Language.translate("how_to_play.menu.line2"), screen, 4, 5 * 8, 0x949494);
        this.font.draw(Language.translate("how_to_play.menu.line3"), screen, 4, 6 * 8, 0x949494);
        this.font.draw(Language.translate("how_to_play.menu.line4"), screen, 4, 7 * 8, 0x949494);
        this.font.draw(Language.translate("how_to_play.menu.line5"), screen, 4, 8 * 8, 0x949494);
        this.font.draw(Language.translate("how_to_play.menu.line6"), screen, 4, 9 * 8, 0x949494);
        this.font.draw(Language.translate("how_to_play.menu.line7"), screen, 4, 10 * 8, 0x949494);
        this.font.draw(Language.translate("how_to_play.menu.line8"), screen, 4, 11 * 8, 0x949494);
        this.font.draw(Language.translate("how_to_play.menu.line9"), screen, 4, 12 * 8, 0x949494);
        this.font.draw(Language.translate("how_to_play.menu.line10"), screen, 4, 13 * 8, 0x949494);
    }
}
