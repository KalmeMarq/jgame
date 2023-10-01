package com.mojang.ld22.item;

import com.mojang.ld22.Game;
import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.gfx.Screen;

import java.util.Random;

public class ToolItem extends Item {
    private final Random random = new Random();

    public static final int MAX_LEVEL = 5;
    public static final String[] LEVEL_NAMES = {
        "Wood", "Rock", "Iron", "Gold", "Gem"
    };

    public ToolType type;
    public int level;

    public ToolItem(ToolType type, int level) {
        this(ToolItem.LEVEL_NAMES[level] + " " + type.name, type, level);
    }

    public ToolItem(String name, ToolType type, int level) {
        super(name, type.sprite + 7 * 32);
        this.type = type;
        this.level = level;
        this.maxStackSize = 1;
    }

    public void renderIcon(Screen screen, int x, int y) {
        screen.renderSprite(x, y, this.getSprite() + 32 * this.level, 2, 0);
    }

    public void renderInventory(Screen screen, int x, int y) {
        screen.renderSprite(x, y, this.getSprite() + 32 * this.level, 2, 0);
        Game.getInstance().font.draw(this.getName(), screen, x + 8, y, 0xFFFFFF);
    }

    public void onTake(ItemEntity itemEntity) {
    }

    public boolean canAttack() {
        return true;
    }

    public int getAttackDamageBonus(Entity e) {
        if (this.type == ToolType.axe) {
            return (this.level + 1) * 2 + this.random.nextInt(4);
        }
        if (this.type == ToolType.sword) {
            return (this.level + 1) * 3 + this.random.nextInt(2 + this.level * this.level * 2);
        }
        return 1;
    }

    public boolean matches(Item item) {
        if (item instanceof ToolItem other) {
            if (other.type != this.type) {
                return false;
            }
            return other.level == this.level;
        }
        return false;
    }
}
