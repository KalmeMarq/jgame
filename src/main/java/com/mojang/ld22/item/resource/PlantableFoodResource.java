package com.mojang.ld22.item.resource;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;

import java.util.Arrays;
import java.util.List;

public class PlantableFoodResource extends Resource {
    private final List<Tile> sourceTiles;
    private final Tile targetTile;
    private final int heal;
    private final int staminaCost;

    public PlantableFoodResource(String name, int sprite, int heal, int staminaCost, Tile targetTile, List<Tile> sourceTiles) {
        super(name, sprite);
        this.heal = heal;
        this.staminaCost = staminaCost;
        this.sourceTiles = sourceTiles;
        this.targetTile = targetTile;
    }

    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
        if (this.sourceTiles.contains(tile)) {
            level.setTile(xt, yt, this.targetTile, 0);
            return true;
        } else if (player.health < player.maxHealth && player.payStamina(this.staminaCost)) {
            player.heal(this.heal);
            return true;
        }
        return false;
    }
}
