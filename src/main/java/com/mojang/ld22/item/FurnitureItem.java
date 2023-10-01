package com.mojang.ld22.item;

import com.mojang.ld22.Game;
import com.mojang.ld22.Language;
import com.mojang.ld22.entity.Furniture;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;

public class FurnitureItem extends Item {
    public Furniture furniture;
    public boolean placed = false;

    public FurnitureItem(String name, Furniture furniture) {
        super(name, furniture.itemSprite);
        this.furniture = furniture;
    }

    public int getSprite() {
        return this.furniture.itemSprite;
    }

    public void renderIcon(Screen screen, int x, int y) {
        screen.renderSprite(x, y, this.getSprite(), 2, 0);
    }

    public void renderInventory(Screen screen, int x, int y) {
        screen.renderSprite(x, y, this.getSprite(), 2, 0);
        Game.getInstance().font.draw(Language.translate(this.furniture.name), screen, x + 8, y, 0xFFFFFF);
    }

    public void onTake(ItemEntity itemEntity) {
    }

    public boolean canAttack() {
        return false;
    }

    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
        if (tile.mayPass(level, xt, yt, this.furniture)) {
            this.furniture.x = xt * 16 + 8;
            this.furniture.y = yt * 16 + 8;
            level.add(this.furniture);
            this.placed = true;
            return true;
        }
        return false;
    }

    public boolean isDepleted() {
        return this.placed;
    }

    public String getName() {
        return this.furniture.name;
    }
}
