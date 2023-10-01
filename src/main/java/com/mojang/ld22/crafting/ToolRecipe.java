package com.mojang.ld22.crafting;

import com.mojang.ld22.Game;
import com.mojang.ld22.Language;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;

public class ToolRecipe extends Recipe {
    private final ToolType type;
    private final int level;

    public ToolRecipe(ToolType type, int level) {
        super(new ToolItem(type, level));
        this.type = type;
        this.level = level;
    }

    @Override
    public void renderInventory(Screen screen, int x, int y) {
        screen.renderSprite(x, y, this.resultTemplate.getSprite() + this.level * 32, 2, 0);
        int textColor = this.canCraft ? 0xFFFFFF : 0x545454;
        Game.getInstance().font.draw(Language.translate(this.resultTemplate.getName()), screen, x + 8, y, textColor);
    }

    public void craft(Player player) {
        player.inventory.add(0, new ToolItem(this.type, this.level));
    }
}
