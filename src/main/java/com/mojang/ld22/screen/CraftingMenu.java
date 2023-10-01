package com.mojang.ld22.screen;

import com.mojang.ld22.Language;
import com.mojang.ld22.crafting.Recipe;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.sound.Sound;

import java.util.ArrayList;
import java.util.List;

public class CraftingMenu extends Menu {
    private final Player player;
    private int selected = 0;

    private final List<Recipe> recipes;

    public CraftingMenu(List<Recipe> recipes, Player player) {
        this.recipes = new ArrayList<>(recipes);
        this.player = player;

        for (int i = 0; i < recipes.size(); i++) {
            this.recipes.get(i).checkCanCraft(player);
        }

        this.recipes.sort((r1, r2) -> {
            if (r1.canCraft && !r2.canCraft) {
                return -1;
            }
            if (!r1.canCraft && r2.canCraft) {
                return 1;
            }
            return 0;
        });
    }

    public void tick() {
        if (this.input.menu.clicked) {
            this.game.setMenu(null);
        }

        if (this.input.up.clicked) {
            this.selected--;
        }
        if (this.input.down.clicked) {
            this.selected++;
        }

        int len = this.recipes.size();
        if (len == 0) {
            this.selected = 0;
        }
        if (this.selected < 0) {
            this.selected += len;
        }
        if (this.selected >= len) {
            this.selected -= len;
        }

        if (this.input.attack.clicked && len > 0) {
            Recipe r = this.recipes.get(this.selected);
            r.checkCanCraft(this.player);
            if (r.canCraft) {
                r.deductCost(this.player);
                r.craft(this.player);
                Sound.play(Sound.Event.CRAFT, 1.0f);
            }
            for (Recipe recipe : this.recipes) {
                recipe.checkCanCraft(this.player);
            }
        }
    }

    public void render(Screen screen) {
        this.font.renderFrame(screen, Language.translate("crafting.menu.have"), 12, 1, 19, 3);
        this.font.renderFrame(screen, Language.translate("crafting.menu.cost"), 12, 4, 19, 11);
        this.font.renderFrame(screen, Language.translate("crafting.menu.title"), 0, 1, 11, 11);
        this.renderItemList(screen, 0, 1, 11, 11, this.recipes, this.selected);

        if (!this.recipes.isEmpty()) {
            Recipe recipe = this.recipes.get(this.selected);
            int hasResultItems = this.player.inventory.count(recipe.resultTemplate);
            int xo = 13 * 8;
            screen.renderSprite(xo, 2 * 8, recipe.resultTemplate.getSprite(), 2, 0);
            this.font.draw(String.valueOf(hasResultItems), screen, xo + 8, 2 * 8, 0xFFFFFF);

            List<Item> costs = recipe.costs;
            for (int i = 0; i < costs.size(); i++) {
                Item item = costs.get(i);
                int yo = (5 + i) * 8;
                screen.renderSprite(xo, yo, item.getSprite(), 2, 0);
                int requiredAmt = 1;
                if (item instanceof ResourceItem) {
                    requiredAmt = ((ResourceItem) item).count;
                }
                int has = this.player.inventory.count(item);
                int color = 0xFFFFFF;
                if (has < requiredAmt) {
                    color = 0x666666;
                }
                if (has > 99) {
                    has = 99;
                }
                this.font.draw(requiredAmt + "/" + has, screen, xo + 8, yo, color);
            }
        }
    }
}
