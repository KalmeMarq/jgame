package com.mojang.ld22.screen;

import com.mojang.ld22.Language;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.sound.Sound;

public class PauseMenu extends Menu {
    public PauseMenu() {
        this.selectEntries.add(new SelectEntry(Language.translate("pause.menu.return_to_game"), (entry) -> {
            Sound.play(Sound.Event.TEST, 1.0f);
            this.game.setMenu(null);
        }));
        this.selectEntries.add(new SelectEntry(Language.translate("menu.options"), (entry) -> {
            Sound.play(Sound.Event.TEST, 1.0f);
            this.game.setMenu(new OptionsMenu(this));
        }));
        this.selectEntries.add(new SelectEntry(Language.translate("pause.menu.quit_to_title"), (entry) -> {
            Sound.play(Sound.Event.TEST, 1.0f);
            this.game.leaveWorld();
            this.game.setMenu(new TitleMenu());
        }));
    }

    @Override
    public void tick() {
        if (this.input.menu.clicked) {
            this.game.setMenu(null);
        }
        super.tick();
    }

    @Override
    public void render(Screen screen) {
        this.font.renderFrame(screen, Language.translate("pause.menu.title"), 0, 3, 19, 8);

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
