package com.mojang.ld22.screen;

import com.mojang.ld22.Game;
import com.mojang.ld22.InputHandler;
import com.mojang.ld22.Language;
import com.mojang.ld22.gfx.Screen;

public class OptionsMenu extends Menu {
    private final Menu parentMenu;

    public OptionsMenu(Menu parentMenu) {
        this.parentMenu = parentMenu;
    }

    @Override
    public void init(Game game, InputHandler input) {
        super.init(game, input);
        this.selectEntries.add(new SelectEntry("Sound: " + (this.game.settings.sound ? "ON" : "OFF"), (entry) -> {
            this.game.settings.sound = !this.game.settings.sound;
            entry.setText("Sound: " + (this.game.settings.sound ? "ON" : "OFF"));
        }));

        this.selectEntries.add(new SelectEntry("Done", (entry) -> {
            this.game.setMenu(this.parentMenu);
        }));
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(Screen screen) {
        if (this.game.player != null) {
            this.font.renderFrame(screen, Language.translate("menu.options"), 0, 3, 19, 8);
        } else {
            screen.clear(0);
        }

        for (int i = 0; i < this.selectEntries.size(); i++) {
            String msg = this.selectEntries.get(i).getText();
            int color = 0x666666;
            if (i == this.selectedEntryIndex) {
                msg = "> " + msg + " <";
                color = 0xFFFFFF;
            }
            this.font.drawCentered(msg, screen, screen.w / 2, 40 + i * 8, color);
        }
    }
}
