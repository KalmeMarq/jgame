package com.mojang.ld22.crafting;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;

public class ToolRecipe extends Recipe {
    private final ToolType type;
    private final int level;

    public ToolRecipe(ToolType type, int level) {
        super(new ToolItem(ToolItem.LEVEL_NAMES[level] + " " + type.name, type, level));
        this.type = type;
        this.level = level;
    }

    public void craft(Player player) {
        player.inventory.add(0, new ToolItem(ToolItem.LEVEL_NAMES[this.level] + " " + this.type.name, this.type, this.level));
    }
}
