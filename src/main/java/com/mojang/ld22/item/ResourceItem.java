package com.mojang.ld22.item;

import com.mojang.ld22.Game;
import com.mojang.ld22.Language;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;

public class ResourceItem extends Item {
    public Resource resource;
    public int count = 1;

    public ResourceItem(Resource resource) {
        super(resource.name, resource.sprite);
        this.resource = resource;
    }

    public ResourceItem(Resource resource, int count) {
        super(resource.name, resource.sprite);
        this.resource = resource;
        this.count = count;
    }

    public void renderIcon(Screen screen, int x, int y) {
        screen.renderSprite(x, y, this.resource.sprite, 2, 0);
    }

    public void renderInventory(Screen screen, int x, int y) {
        screen.renderSprite(x, y, this.resource.sprite, 2, 0);
        Game.getInstance().font.draw(Language.translate(this.resource.name), screen, x + 32, y, 0xFFFFFF);
        int cc = this.count;
        if (cc > 999) {
            cc = 999;
        }
        Game.getInstance().font.draw(String.valueOf(cc), screen, x + 8, y, 0xEEEEEE);
    }

    public void onTake(ItemEntity itemEntity) {
    }

    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
        if (this.resource.interactOn(tile, level, xt, yt, player, attackDir)) {
            this.count--;
            return true;
        }
        return false;
    }

    public boolean isDepleted() {
        return this.count <= 0;
    }
}
