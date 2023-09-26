package com.mojang.ld22.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.ld22.Game;
import com.mojang.ld22.InputHandler;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;

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
        if (this.input.up.clicked) {
            this.selectedEntryIndex--;
        }
        if (this.input.down.clicked) {
            this.selectedEntryIndex++;
        }

        int len = this.selectEntries.size();
        if (this.selectedEntryIndex < 0) {
            this.selectedEntryIndex += len;
        }
        if (this.selectedEntryIndex >= len) {
            this.selectedEntryIndex -= len;
        }

        if (this.input.attack.clicked || this.input.menu.clicked) {
            this.selectEntries.get(this.selectedEntryIndex).act();
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
            this.font.draw(">", screen, (xo) * 8, yy * 8, Color.get(5, 555, 555, 555));
            this.font.draw("<", screen, (xo + w) * 8, yy * 8, Color.get(5, 555, 555, 555));
        }
    }

    public static class SelectEntry {
        private final String text;
        private final Runnable onAction;

        public SelectEntry(String text, Runnable onAction) {
            this.text = text;
            this.onAction = onAction;
        }

        public void act() {
            this.onAction.run();
        }

        public String getText() {
            return this.text;
        }
    }
}
