package com.mojang.ld22.entity;

public class Lantern extends Furniture {
    public Lantern() {
        super("minicraft.lantern", 7 * 32 + 10);
        this.sprite = 24 * 32 + 29;
        this.xr = 3;
        this.yr = 2;
    }

    public int getLightRadius() {
        return 8;
    }
}
