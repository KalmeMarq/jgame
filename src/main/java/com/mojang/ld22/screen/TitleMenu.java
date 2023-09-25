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
    private int selected = 0;
    private String splash;

    private static final String[] options = {"Start game", "How to play", "About", "Quit"};

    public TitleMenu() {
        if (TitleMenu.splashes.size() == 0) {
            TitleMenu.splashes.addAll(StringHelper.readLines(TitleMenu.class.getResourceAsStream("/splashes.txt")));
        }

        if (TitleMenu.splashes.size() == 0) {
            this.splash = "Missingno!";
        } else {
            this.splash = TitleMenu.splashes.get(TitleMenu.RANDOM.nextInt(TitleMenu.splashes.size()));
        }
    }

    public void tick() {
        if (this.input.up.clicked) {
            this.selected--;
        }
        if (this.input.down.clicked) {
            this.selected++;
        }

        int len = TitleMenu.options.length;
        if (this.selected < 0) {
            this.selected += len;
        }
        if (this.selected >= len) {
            this.selected -= len;
        }

        if (this.input.attack.clicked || this.input.menu.clicked) {
            if (this.selected == 0) {
                Sound.test.play();
                this.game.resetGame();
                this.game.setMenu(null);
            }
            if (this.selected == 1) {
                this.game.setMenu(new InstructionsMenu(this));
            }
            if (this.selected == 2) {
                this.game.setMenu(new AboutMenu(this));
            }
            if (this.selected == 3) {
                this.game.stop();
            }
        }
    }

    public void render(Screen screen) {
        screen.clear(0);

        int h = 2;
        int w = 13;
        int titleColor = Color.get(0, 010, 131, 551);
        int xo = (screen.w - w * 8) / 2;
        int yo = 24;
        for (int y = 0; y < h; y++) {
            for (int x = 0;
                 x < w;
                 x++) {
                screen.render(xo + x * 8, yo + y * 8, x + (y + 6) * 32, titleColor, 0);
            }
        }

        this.font.drawCentered(this.splash, screen, screen.w / 2, 55, Color.get(0, 550, 550, 550));

        for (int i = 0;
             i < options.length;
             i++) {
            String msg = TitleMenu.options[i];
            int col = Color.get(0, 222, 222, 222);
            if (i == this.selected) {
                msg = "> " + msg + " <";
                col = Color.get(0, 555, 555, 555);
            }
            this.font.draw(msg, screen, (screen.w - msg.length() * 8) / 2, 10 + (8 + i) * 8, col);
        }

        this.font.draw("(Arrow keys,X and C)", screen, 0, screen.h - 8, Color.get(0, 111, 111, 111));
    }
}
