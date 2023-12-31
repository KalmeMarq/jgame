package com.mojang.ld22.item.resource;

import java.util.Arrays;
import java.util.List;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;

public class PlantableResource extends Resource {
    private final List<Tile> sourceTiles;
    private final Tile targetTile;

    public PlantableResource(String name, int sprite, Tile targetTile, Tile... sourceTiles1) {
        this(name, sprite, targetTile, Arrays.asList(sourceTiles1));
    }

    public PlantableResource(String name, int sprite, Tile targetTile, List<Tile> sourceTiles) {
        super(name, sprite);
        this.sourceTiles = sourceTiles;
        this.targetTile = targetTile;
    }

    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
        if (this.sourceTiles.contains(tile)) {
            level.setTile(xt, yt, this.targetTile, 0);
            return true;
        }
        return false;
    }
}
