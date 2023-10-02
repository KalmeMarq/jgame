package com.mojang.ld22.entity;

import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.sound.Sound;
import me.kalmemarq.jgame.bso.BsoMap;

public class ItemEntity extends Entity {
    private int lifeTime;
    public int hurtTime = 0;
    public double xa, ya, za;
    public double xx, yy, zz;
    public Item item;
    private int time = 0;

    public ItemEntity() {
    }

    public ItemEntity(Item item, int x, int y) {
        this.item = item;
        this.xx = this.x = x;
        this.yy = this.y = y;
        this.xr = 3;
        this.yr = 3;

        this.zz = 2;
        this.xa = this.random.nextGaussian() * 0.3;
        this.ya = this.random.nextGaussian() * 0.2;
        this.za = this.random.nextFloat() * 0.7 + 1;

        this.lifeTime = 60 * 10 + this.random.nextInt(60);
    }

    @Override
    public void writeData(BsoMap map) {
        super.writeData(map);
        map.putInt("lifeTime", this.lifeTime);
        map.putInt("hurtTime", this.hurtTime);
        map.putInt("time", this.time);
        map.putDouble("xa", this.xa);
        map.putDouble("ya", this.ya);
        map.putDouble("za", this.za);
        map.putDouble("xx", this.xx);
        map.putDouble("yy", this.yy);
        map.putDouble("zz", this.zz);
        map.putString("item", this.item.getName());
        if (this.item instanceof ResourceItem resourceItem) {
            map.putInt("itemCount", resourceItem.count);
        }
    }

    @Override
    public void loadData(BsoMap map) {
        super.loadData(map);
        this.lifeTime = map.getInt("lifeTime");
        this.hurtTime = map.getInt("hurtTime");
        this.time = map.getInt("time");
        this.xa = map.getDouble("xa");
        this.ya = map.getDouble("ya");
        this.za = map.getDouble("za");
        this.xx = map.getDouble("xx");
        this.yy = map.getDouble("yy");
        this.zz = map.getDouble("zz");
        String name = map.getString("item");
    }

    public void tick() {
        this.time++;
        if (this.time >= this.lifeTime) {
            this.remove();
            return;
        }
        this.xx += this.xa;
        this.yy += this.ya;
        this.zz += this.za;
        if (this.zz < 0) {
            this.zz = 0;
            this.za *= -0.5;
            this.xa *= 0.6;
            this.ya *= 0.6;
        }
        this.za -= 0.15;
        int ox = this.x;
        int oy = this.y;
        int nx = (int) this.xx;
        int ny = (int) this.yy;
        int expectedx = nx - this.x;
        int expectedy = ny - this.y;
        this.move(nx - this.x, ny - this.y);
        int gotx = this.x - ox;
        int goty = this.y - oy;
        this.xx += gotx - expectedx;
        this.yy += goty - expectedy;

        if (this.hurtTime > 0) {
            this.hurtTime--;
        }
    }

    public boolean isBlockableBy(Mob mob) {
        return false;
    }

    public void render(Screen screen) {
        if (this.time >= this.lifeTime - 6 * 20) {
            if (this.time / 6 % 2 == 0) {
                return;
            }
        }
        screen.renderSprite(this.x - 4, this.y - 4, this.item.getSprite(), 2, 0);
        screen.renderSprite(this.x - 4, this.y - 4 - (int) (this.zz), this.item.getSprite(),2, 0);
    }

    protected void touchedBy(Entity entity) {
        if (this.time > 30) {
            entity.touchItem(this);
        }
    }

    public void take(Player player) {
        Sound.play(Sound.Event.PICKUP, 1.0f);
        player.score++;
        this.item.onTake(this);
        this.remove();
    }
}
