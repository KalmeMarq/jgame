package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;

public class DeadMenu extends Menu {
    private int inputDelay = 60;

    public DeadMenu() {
    }

    public void tick() {
        if (this.inputDelay > 0) {
            this.inputDelay--;
        } else if (this.input.attack.clicked || this.input.menu.clicked) {
            this.game.leaveWorld();
            this.game.setMenu(new TitleMenu());
        }
    }

    public void render(Screen screen) {
        this.font.renderFrame(screen, "", 1, 3, 18, 9);
        this.font.draw("You died! Aww!", screen, 2 * 8, 4 * 8, Color.get(-1, 555, 555, 555));

        int seconds = this.game.gameTime / 60;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        minutes %= 60;
        seconds %= 60;

        String timeString;
        if (hours > 0) {
            timeString = hours + "h" + (minutes < 10 ? "0" : "") + minutes + "m";
        } else {
            timeString = minutes + "m " + (seconds < 10 ? "0" : "") + seconds + "s";
        }
        this.font.draw("Time:", screen, 2 * 8, 5 * 8, Color.get(-1, 555, 555, 555));
        this.font.draw(timeString, screen, (2 + 5) * 8, 5 * 8, Color.get(-1, 550, 550, 550));
        this.font.draw("Score:", screen, 2 * 8, 6 * 8, Color.get(-1, 555, 555, 555));
        this.font.draw("" + this.game.player.score, screen, (2 + 6) * 8, 6 * 8, Color.get(-1, 550, 550, 550));
        this.font.draw("Press C to lose", screen, 2 * 8, 8 * 8, Color.get(-1, 333, 333, 333));
    }
}
