package com.mojang.ld22.item.resource;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.SpriteSheet;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;

public class Resource {
    public static Resource wood = new Resource("Wood", 1 + 6 * 32);
    public static Resource stone = new Resource("Stone", 11 + 7 * 32);
    public static Resource flower = new PlantableResource("Flower", 7 * 32, Tile.flower, Tile.grass);
    public static Resource acorn = new PlantableResource("Acorn", 3 + 6 * 32, Tile.treeSapling, Tile.grass);
    public static Resource dirt = new PlantableResource("Dirt", 2 + 6 * 32, Tile.dirt, Tile.hole, Tile.water, Tile.lava);
    public static Resource sand = new PlantableResource("Sand", 6 + 8 * 32, Tile.sand, Tile.grass, Tile.dirt);
    public static Resource cactusFlower = new PlantableResource("Cactus", 4 + 6 * 32, Tile.cactusSapling, Tile.sand);
    public static Resource seeds = new PlantableResource("Seeds", 5 + 6 * 32, Tile.wheat, Tile.farmland);
    public static Resource wheat = new Resource("Wheat", 6 + 6 * 32);
    public static Resource bread = new FoodResource("Bread", 8 + 6 * 32, 2, 5);
    public static Resource apple = new FoodResource("Apple", 9 + 6 * 32, 1, 5);

    public static Resource coal = new Resource("COAL", SpriteSheet.uvTile(96, 56));
    public static Resource ironOre = new Resource("I.ORE", SpriteSheet.uvTile(64, 64));
    public static Resource goldOre = new Resource("G.ORE", SpriteSheet.uvTile(56, 64));
    public static Resource ironIngot = new Resource("IRON", SpriteSheet.uvTile(88, 48));
    public static Resource goldIngot = new Resource("GOLD", SpriteSheet.uvTile(40, 64));

    public static Resource slime = new Resource("SLIME", SpriteSheet.uvTile(80, 48));
    public static Resource glass = new Resource("glass", SpriteSheet.uvTile(96, 48));
    public static Resource cloth = new Resource("cloth", SpriteSheet.uvTile(104, 56));
    public static Resource cloud = new PlantableResource("cloud", 2 + 4 * 32, Tile.cloud, Tile.infiniteFall);
    public static Resource gem = new Resource("gem", SpriteSheet.uvTile(104, 48));

    public final String name;
    public final int sprite;

    public Resource(String name, int sprite) {
        if (name.length() > 6) {
            throw new RuntimeException("Name cannot be longer than six characters!");
        }
        this.name = name;
        this.sprite = sprite;
    }

    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
        return false;
    }
}
