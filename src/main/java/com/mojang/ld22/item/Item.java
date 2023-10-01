package com.mojang.ld22.item;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;
import com.mojang.ld22.screen.ListItem;

public class Item implements ListItem {
    public final String name;
    public final int sprite;
    public int maxStackSize = 256;

    public Item(String name, int sprite) {
        this.name = name;
        this.sprite = sprite;
    }

    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    public int getMaxStackSize() {
        return this.maxStackSize;
    }

    public int getSprite() {
        return this.sprite;
    }

    public String getTranslationKey() {
        return this.getName();
    }

    public void onTake(ItemEntity itemEntity) {
    }

    public void renderInventory(Screen screen, int x, int y) {
    }

    public boolean interact(Player player, Entity entity, int attackDir) {
        return false;
    }

    public void renderIcon(Screen screen, int x, int y) {
    }

    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
        return false;
    }

    public boolean isDepleted() {
        return false;
    }

    public boolean canAttack() {
        return false;
    }

    public int getAttackDamageBonus(Entity e) {
        return 0;
    }

    public String getName() {
        return this.name;
    }

    public boolean matches(Item item) {
        return item.getClass() == this.getClass();
    }
}
