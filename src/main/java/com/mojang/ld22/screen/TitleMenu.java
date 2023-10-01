package com.mojang.ld22.screen;

import com.mojang.ld22.Language;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.sound.Sound;
import me.kalmemarq.jgame.StringHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TitleMenu extends Menu {
    private static final Random RANDOM = new Random();
    private static final List<String> splashes = new ArrayList<>();
    private final String splash;

    public TitleMenu() {
        if (TitleMenu.splashes.isEmpty()) {
            TitleMenu.splashes.addAll(StringHelper.readLines(TitleMenu.class.getResourceAsStream("/splashes.txt")));
        }

        if (TitleMenu.splashes.isEmpty()) {
            this.splash = "Missingno!";
        } else {
            this.splash = TitleMenu.splashes.get(TitleMenu.RANDOM.nextInt(TitleMenu.splashes.size()));
        }

        this.selectEntries.add(new SelectEntry(Language.translate("menu.start_game"), () -> {
            Sound.play(Sound.Event.TEST, 1.0f);
            this.game.resetGame();
            this.game.setMenu(null);
        }));
        this.selectEntries.add(new SelectEntry(Language.translate("menu.how_to_play"), () -> {
            this.game.setMenu(new InstructionsMenu(this));
        }));
        this.selectEntries.add(new SelectEntry(Language.translate("menu.about"), () -> {
            this.game.setMenu(new AboutMenu(this));
        }));
        this.selectEntries.add(new SelectEntry(Language.translate("menu.options"), () -> {
            this.game.setMenu(new OptionsMenu(this));
        }));
        this.selectEntries.add(new SelectEntry(Language.translate("menu.quit"), () -> {
            this.game.stop();
        }));
    }

    public void render(Screen screen) {
        screen.clear(0);

        screen.renderTextured((screen.w - 104) / 2, 19, 104, 16, 0, 0, 2, 0xFFFFFF, 0);

        this.font.drawCentered(this.splash, screen, screen.w / 2, 50, 0xFFFFFF);

        for (int i = 0; i < this.selectEntries.size(); i++) {
            String msg = this.selectEntries.get(i).getText();
            int color = 0x666666;
            if (i == this.selectedEntryIndex) {
                msg = "> " + msg + " <";
                color = 0xFFFFFF;
            }
            this.font.drawCentered(msg, screen, screen.w / 2, 5 + (8 + i) * 8, color);
        }

        this.font.draw(Language.translate("title.menu.tip"), screen, 0, screen.h - 8, 0x383838);
    }
}
