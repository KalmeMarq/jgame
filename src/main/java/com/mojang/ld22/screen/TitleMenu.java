package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Color;
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
        if (TitleMenu.splashes.size() == 0) {
            TitleMenu.splashes.addAll(StringHelper.readLines(TitleMenu.class.getResourceAsStream("/splashes.txt")));
        }

        if (TitleMenu.splashes.size() == 0) {
            this.splash = "Missingno!";
        } else {
            this.splash = TitleMenu.splashes.get(TitleMenu.RANDOM.nextInt(TitleMenu.splashes.size()));
        }

        this.selectEntries.add(new SelectEntry("Start Game", () -> {
            Sound.play(Sound.Event.TEST, 1.0f);
            this.game.resetGame();
            this.game.setMenu(null);
        }));
        this.selectEntries.add(new SelectEntry("How to play", () -> {
            this.game.setMenu(new InstructionsMenu(this));
        }));
        this.selectEntries.add(new SelectEntry("About", () -> {
            this.game.setMenu(new AboutMenu(this));
        }));
        this.selectEntries.add(new SelectEntry("Quit", () -> {
            this.game.stop();
        }));
    }

    public void render(Screen screen) {
        screen.clear(0);

        int h = 2;
        int w = 13;
        int xo = (screen.w - w * 8) / 2;
        int yo = 24;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                screen.renderSprite(xo + x * 8, yo + y * 8, x + (y) * 32, 2, 0xFF_FFFFFF, 0);
            }
        }

        this.font.drawCentered(this.splash, screen, screen.w / 2, 55, 0xFFFFFF);

        for (int i = 0; i < this.selectEntries.size(); i++) {
            String msg = this.selectEntries.get(i).getText();
            int col = 0x9A9A9A;
            if (i == this.selectedEntryIndex) {
                msg = "> " + msg + " <";
                col = 0xFFFFFF;
            }
            this.font.draw(msg, screen, (screen.w - msg.length() * 8) / 2, 10 + (8 + i) * 8, col);
        }

        this.font.draw("(Arrow keys,X and C)", screen, 0, screen.h - 8, 0x454545);
    }
}
