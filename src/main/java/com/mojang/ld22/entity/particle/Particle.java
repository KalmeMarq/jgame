package com.mojang.ld22.entity.particle;

import com.mojang.ld22.entity.Entity;

public class Particle extends Entity {
    protected int time = 0;

    public Particle() {
    }

    @Override
    public void tick() {
        ++this.time;
    }
}
