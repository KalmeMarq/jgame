package com.mojang.ld22.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mojang.ld22.Game;
import com.mojang.ld22.InputHandler;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.sound.Sound;

public class Menu {
    protected Game game;
    protected InputHandler input;
    protected Font font;
    protected int selectedEntryIndex = 0;
    protected List<SelectEntry> selectEntries = new ArrayList<>();

    public void init(Game game, InputHandler input) {
        this.input = input;
        this.game = game;
        this.font = game.font;
    }

    public void tick() {
        int len = this.selectEntries.size();
        if (len > 0) {
            if (this.input.up.clicked) {
                this.selectedEntryIndex = Math.floorMod(this.selectedEntryIndex - 1, len);
                Sound.play(Sound.Event.CRAFT, 1.0f);
            }
            if (this.input.down.clicked) {
                this.selectedEntryIndex = Math.floorMod(this.selectedEntryIndex + 1, len);
                Sound.play(Sound.Event.TEST, 1.0f);
            }

            if (this.input.attack.clicked || this.input.menu.clicked) {
                this.selectEntries.get(this.selectedEntryIndex).act();
            }
        }
    }

    public void render(Screen screen) {
    }

    public void renderItemList(Screen screen, int xo, int yo, int x1, int y1, List<? extends ListItem> listItems, int selected) {
        boolean renderCursor = true;
        if (selected < 0) {
            selected = -selected - 1;
            renderCursor = false;
        }
        int w = x1 - xo;
        int h = y1 - yo - 1;
        int i0 = 0;
        int i1 = listItems.size();
        if (i1 > h) {
            i1 = h;
        }
        int io = selected - h / 2;
        if (io > listItems.size() - h) {
            io = listItems.size() - h;
        }
        if (io < 0) {
            io = 0;
        }

        for (int i = i0; i < i1; i++) {
            listItems.get(i + io).renderInventory(screen, (1 + xo) * 8, (i + 1 + yo) * 8);
        }

        if (renderCursor) {
            int yy = selected + 1 - io + yo;
            this.font.draw(">", screen, (xo) * 8, yy * 8, 0xFFFFFF);
            this.font.draw("<", screen, (xo + w) * 8, yy * 8, 0xFFFFFF);
        }
    }

    public static class SelectEntry {
        private String text;
        private final Consumer<SelectEntry> onAction;

        public SelectEntry(String text, Consumer<SelectEntry> onAction) {
            this.text = text;
            this.onAction = onAction;
        }

        public void setText(String text) {
            this.text = text;
        }

        public void act() {
            this.onAction.accept(this);
        }

        public String getText() {
            return this.text;
        }
    }
}
