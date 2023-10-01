package com.mojang.ld22.crafting;

import com.mojang.ld22.Game;
import com.mojang.ld22.Language;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.screen.ListItem;

import java.util.ArrayList;
import java.util.List;

public abstract class Recipe implements ListItem {
    public List<Item> costs = new ArrayList<>();
    public boolean canCraft = false;
    public Item resultTemplate;

    public Recipe(Item resultTemplate) {
        this.resultTemplate = resultTemplate;
    }

    public void addCost(Resource resource, int count) {
        this.costs.add(new ResourceItem(resource, count));
    }

    public void checkCanCraft(Player player) {
        for (Item item : this.costs) {
            if (item instanceof ResourceItem ri) {
                if (!player.inventory.hasResources(ri.resource, ri.count)) {
                    this.canCraft = false;
                    return;
                }
            }
        }
        this.canCraft = true;
    }

    public void renderInventory(Screen screen, int x, int y) {
        screen.renderSprite(x, y, this.resultTemplate.getSprite(), 2, 0);
        int textColor = this.canCraft ? 0xFFFFFF : 0x545454;
        Game.getInstance().font.draw(Language.translate(this.resultTemplate.getName()), screen, x + 8, y, textColor);
    }

    public abstract void craft(Player player);

    public void deductCost(Player player) {
        for (Item item : this.costs) {
            if (item instanceof ResourceItem ri) {
                player.inventory.removeResource(ri.resource, ri.count);
            }
        }
    }
}
