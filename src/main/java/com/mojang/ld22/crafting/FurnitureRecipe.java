package com.mojang.ld22.crafting;

import com.mojang.ld22.entity.Furniture;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.item.FurnitureItem;

import java.lang.reflect.InvocationTargetException;

public class FurnitureRecipe extends Recipe {
    private final Class<? extends Furniture> clazz;

    public FurnitureRecipe(Class<? extends Furniture> clazz) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        super(new FurnitureItem("f", clazz.getConstructor(new Class[0]).newInstance()));
        this.clazz = clazz;
    }

    public void craft(Player player) {
        try {
            player.inventory.add(0, new FurnitureItem("f", this.clazz.getConstructor(new Class[0]).newInstance()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
