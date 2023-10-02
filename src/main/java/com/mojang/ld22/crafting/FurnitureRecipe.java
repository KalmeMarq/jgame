package com.mojang.ld22.crafting;

import com.mojang.ld22.entity.EntityType;
import com.mojang.ld22.entity.Furniture;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.item.FurnitureItem;

public class FurnitureRecipe extends Recipe {
    private final EntityType<? extends Furniture> clazz;

    public FurnitureRecipe(EntityType<? extends Furniture> furniture) {
        super(new FurnitureItem("f", furniture.create()));
        this.clazz = furniture;
    }

    public void craft(Player player) {
        try {
            player.inventory.add(0, new FurnitureItem("f", this.clazz.create()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
