package com.mojang.ld22.entity;

import com.mojang.ld22.Game;
import com.mojang.ld22.InputHandler;
import com.mojang.ld22.entity.particle.TextParticle;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.FurnitureItem;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.PowerGloveItem;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;
import com.mojang.ld22.screen.InventoryMenu;
import com.mojang.ld22.sound.Sound;
import me.kalmemarq.jgame.bso.BsoMap;

import java.util.List;

public class Player extends Mob {
    private final InputHandler input;
    private int attackTime, attackDir;

    public Game game;
    public Inventory inventory = new Inventory();
    public Item attackItem;
    public Item activeItem;
    public int stamina;
    public int staminaRecharge;
    public int staminaRechargeDelay;
    public int score;
    public int maxStamina = 10;
    private int onStairDelay;
    public int invulnerableTime = 0;

    public Player(Game game, InputHandler input) {
        this.game = game;
        this.input = input;
        this.x = 24;
        this.y = 24;
        this.stamina = this.maxStamina;
        this.inventory.add(new FurnitureItem("Workbench", new Workbench()));
        this.inventory.add(new PowerGloveItem());
    }

    @Override
    public void writeData(BsoMap map) {
        super.writeData(map);
        map.putInt("attackTime", this.attackTime);
        map.putInt("attackDir", this.attackDir);
        map.putInt("stamina", this.stamina);
        map.putInt("staminaRecharge", this.staminaRecharge);
        map.putInt("staminaRechargeDelay", this.staminaRechargeDelay);
        map.putInt("score", this.score);
        map.putInt("maxStamina", this.maxStamina);
        map.putInt("onStairDelay", this.onStairDelay);
        map.putInt("invulnerableTime", this.invulnerableTime);
    }

    @Override
    public void loadData(BsoMap map) {
        super.loadData(map);
    }

    public void tick() {
        super.tick();

        if (this.invulnerableTime > 0) {
            this.invulnerableTime--;
        }

        Tile onTile = this.level.getTile(this.x >> 4, this.y >> 4);
        if (onTile == Tile.stairsDown || onTile == Tile.stairsUp) {
            if (this.onStairDelay == 0) {
                this.changeLevel((onTile == Tile.stairsUp) ? 1 : -1);
                this.onStairDelay = 10;
                return;
            }
            this.onStairDelay = 10;
        } else {
            if (this.onStairDelay > 0) {
                this.onStairDelay--;
            }
        }

        if (this.stamina <= 0 && this.staminaRechargeDelay == 0 && this.staminaRecharge == 0) {
            this.staminaRechargeDelay = 40;
        }

        if (this.staminaRechargeDelay > 0) {
            this.staminaRechargeDelay--;
        }

        if (this.staminaRechargeDelay == 0) {
            this.staminaRecharge++;
            if (this.isSwimming()) {
                this.staminaRecharge = 0;
            }
            while (this.staminaRecharge > 10) {
                this.staminaRecharge -= 10;
                if (this.stamina < this.maxStamina) {
                    this.stamina++;
                }
            }
        }

        int xa = 0;
        int ya = 0;
        if (this.input.up.down) {
            ya--;
        }
        if (this.input.down.down) {
            ya++;
        }
        if (this.input.left.down) {
            xa--;
        }
        if (this.input.right.down) {
            xa++;
        }
        if (this.isSwimming() && this.tickTime % 60 == 0) {
            if (this.stamina > 0) {
                this.stamina--;
            } else {
                this.hurt(this, 1, this.dir ^ 1);
            }
        }

        if (this.staminaRechargeDelay % 2 == 0) {
            this.move(xa, ya);
        }

        if (this.input.attack.clicked && this.attackTime <= 0) {
            if (this.stamina != 0) {
                this.stamina--;
                this.staminaRecharge = 0;
                this.attack();
            }
        }
        if (this.input.menu.clicked) {
            if (!this.use()) {
                this.game.setMenu(new InventoryMenu(this));
            }
        }
        if (this.attackTime > 0) {
            this.attackTime--;
        }
    }

    private boolean use() {
        int yo = -2;
        if (this.dir == 0 && this.use(this.x - 8, this.y + 4 + yo, this.x + 8, this.y + 12 + yo)) {
            return true;
        }
        if (this.dir == 1 && this.use(this.x - 8, this.y - 12 + yo, this.x + 8, this.y - 4 + yo)) {
            return true;
        }
        if (this.dir == 3 && this.use(this.x + 4, this.y - 8 + yo, this.x + 12, this.y + 8 + yo)) {
            return true;
        }
        if (this.dir == 2 && this.use(this.x - 12, this.y - 8 + yo, this.x - 4, this.y + 8 + yo)) {
            return true;
        }

        int xt = this.x >> 4;
        int yt = (this.y + yo) >> 4;
        int r = 12;
        if (this.attackDir == 0) {
            yt = (this.y + r + yo) >> 4;
        }
        if (this.attackDir == 1) {
            yt = (this.y - r + yo) >> 4;
        }
        if (this.attackDir == 2) {
            xt = (this.x - r) >> 4;
        }
        if (this.attackDir == 3) {
            xt = (this.x + r) >> 4;
        }

        if (xt >= 0 && yt >= 0 && xt < this.level.w && yt < this.level.h) {
            return this.level.getTile(xt, yt).use(this.level, xt, yt, this, this.attackDir);
        }

        return false;
    }

    private void attack() {
        this.walkDist += 8;
        this.attackDir = this.dir;
        this.attackItem = this.activeItem;
        boolean done = false;

        if (this.activeItem != null) {
            this.attackTime = 10;
            int yo = -2;
            int range = 12;
            if (this.dir == 0 && this.interact(this.x - 8, this.y + 4 + yo, this.x + 8, this.y + range + yo)) {
                done = true;
            }
            if (this.dir == 1 && this.interact(this.x - 8, this.y - range + yo, this.x + 8, this.y - 4 + yo)) {
                done = true;
            }
            if (this.dir == 3 && this.interact(this.x + 4, this.y - 8 + yo, this.x + range, this.y + 8 + yo)) {
                done = true;
            }
            if (this.dir == 2 && this.interact(this.x - range, this.y - 8 + yo, this.x - 4, this.y + 8 + yo)) {
                done = true;
            }
            if (done) {
                return;
            }

            int xt = this.x >> 4;
            int yt = (this.y + yo) >> 4;
            int r = 12;
            if (this.attackDir == 0) {
                yt = (this.y + r + yo) >> 4;
            }
            if (this.attackDir == 1) {
                yt = (this.y - r + yo) >> 4;
            }
            if (this.attackDir == 2) {
                xt = (this.x - r) >> 4;
            }
            if (this.attackDir == 3) {
                xt = (this.x + r) >> 4;
            }

            if (xt >= 0 && yt >= 0 && xt < this.level.w && yt < this.level.h) {
                if (this.activeItem.interactOn(this.level.getTile(xt, yt), this.level, xt, yt, this, this.attackDir)) {
                    done = true;
                } else {
                    if (this.level.getTile(xt, yt).interact(this.level, xt, yt, this, this.activeItem, this.attackDir)) {
                        done = true;
                    }
                }
                if (this.activeItem.isDepleted()) {
                    this.activeItem = null;
                }
            }
        }

        if (done) {
            return;
        }

        if (this.activeItem == null || this.activeItem.canAttack()) {
            this.attackTime = 5;
            int yo = -2;
            int range = 20;
            if (this.dir == 0) {
                this.hurt(this.x - 8, this.y + 4 + yo, this.x + 8, this.y + range + yo);
            }
            if (this.dir == 1) {
                this.hurt(this.x - 8, this.y - range + yo, this.x + 8, this.y - 4 + yo);
            }
            if (this.dir == 3) {
                this.hurt(this.x + 4, this.y - 8 + yo, this.x + range, this.y + 8 + yo);
            }
            if (this.dir == 2) {
                this.hurt(this.x - range, this.y - 8 + yo, this.x - 4, this.y + 8 + yo);
            }

            int xt = this.x >> 4;
            int yt = (this.y + yo) >> 4;
            int r = 12;
            if (this.attackDir == 0) {
                yt = (this.y + r + yo) >> 4;
            }
            if (this.attackDir == 1) {
                yt = (this.y - r + yo) >> 4;
            }
            if (this.attackDir == 2) {
                xt = (this.x - r) >> 4;
            }
            if (this.attackDir == 3) {
                xt = (this.x + r) >> 4;
            }

            if (xt >= 0 && yt >= 0 && xt < this.level.w && yt < this.level.h) {
                this.level.getTile(xt, yt).hurt(this.level, xt, yt, this, this.random.nextInt(3) + 1, this.attackDir);
            }
        }
    }

    private boolean use(int x0, int y0, int x1, int y1) {
        List<Entity> entities = this.level.getEntities(x0, y0, x1, y1);
        for (Entity e : entities) {
            if (e != this) {
                if (e.use(this, this.attackDir)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean interact(int x0, int y0, int x1, int y1) {
        List<Entity> entities = this.level.getEntities(x0, y0, x1, y1);
        for (Entity e : entities) {
            if (e != this) {
                if (e.interact(this, this.activeItem, this.attackDir)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void hurt(int x0, int y0, int x1, int y1) {
        List<Entity> entities = this.level.getEntities(x0, y0, x1, y1);
        for (Entity e : entities) {
            if (e != this) {
                e.hurt(this, this.getAttackDamage(e), this.attackDir);
            }
        }
    }

    private int getAttackDamage(Entity e) {
        int dmg = this.random.nextInt(3) + 1;
        if (this.attackItem != null) {
            dmg += this.attackItem.getAttackDamageBonus(e);
        }
        return dmg;
    }

    public void render(Screen screen) {
        int xt = 0;

        int flip1 = (this.walkDist >> 3) & 1;
        int flip2 = (this.walkDist >> 3) & 1;

        if (this.dir == 1) {
            xt += 2;
        }
        if (this.dir > 1) {
            flip1 = 0;
            flip2 = ((this.walkDist >> 4) & 1);
            if (this.dir == 2) {
                flip1 = 1;
            }
            xt += 4 + ((this.walkDist >> 3) & 1) * 2;
        }

        int xo = this.x - 8;
        int yo = this.y - 11;
        if (this.isSwimming()) {
            yo += 4;
            int tl = 15;
            if (this.level.getTile(this.x >> 4, this.y >> 4) == Tile.lava) {
                tl += 1;
            }
            if (this.tickTime / 8 % 2 == 0) {
                tl -= 2;
            }
            tl *= 32;
            screen.renderSprite(xo, yo + 3, tl, 2, 0xFFFFFF, 0);
            screen.renderSprite(xo + 8, yo + 3, tl, 2, 0xFFFFFF, 1);
        }

        if (this.attackTime > 0 && this.attackDir == 1) {
            screen.renderSprite(xo, yo - 4, 1 + 13 * 32, 2, 0);
            screen.renderSprite(xo + 8, yo - 4, 1 + 13 * 32, 2, 1);
            if (this.attackItem != null) {
                this.attackItem.renderIcon(screen, xo + 4, yo - 4);
            }
        }

        boolean hasFurnitureOverHead = this.activeItem instanceof FurnitureItem;
        screen.renderTextured(xo, yo, 16, 8, xt * 8, 176 + (hasFurnitureOverHead ? 16 : 0), 2, 0xFFFFFF, this.hurtTime > 0, flip1);
        if (!this.isSwimming()) {
            screen.renderTextured(xo, yo + 8, 16, 8, xt * 8, 184 + (hasFurnitureOverHead ? 16 : 0), 2, 0xFFFFFF, this.hurtTime > 0, flip2);
        }

        if (this.attackTime > 0 && this.attackDir == 2) {
            screen.renderSprite(xo - 4, yo, 2 + 13 * 32, 2, 1);
            screen.renderSprite(xo - 4, yo + 8, 2 + 13 * 32, 2, 3);
            if (this.attackItem != null) {
                this.attackItem.renderIcon(screen, xo - 4, yo + 4);
            }
        }
        if (this.attackTime > 0 && this.attackDir == 3) {
            screen.renderSprite(xo + 8 + 4, yo, 2 + 13 * 32, 2, 0);
            screen.renderSprite(xo + 8 + 4, yo + 8, 2 + 13 * 32, 2, 2);
            if (this.attackItem != null) {
                this.attackItem.renderIcon(screen, xo + 8 + 4, yo + 4);
            }
        }
        if (this.attackTime > 0 && this.attackDir == 0) {
            screen.renderSprite(xo, yo + 8 + 4, 1 + 13 * 32, 2, 2);
            screen.renderSprite(xo + 8, yo + 8 + 4, 1 + 13 * 32, 2, 3);
            if (this.attackItem != null) {
                this.attackItem.renderIcon(screen, xo + 4, yo + 8 + 4);
            }
        }

        if (this.activeItem instanceof FurnitureItem) {
            Furniture furniture = ((FurnitureItem) this.activeItem).furniture;
            furniture.x = this.x;
            furniture.y = yo;
            furniture.render(screen);
        }
    }

    public void touchItem(ItemEntity itemEntity) {
        itemEntity.take(this);
        this.inventory.add(itemEntity.item);
    }

    public boolean canSwim() {
        return true;
    }

    public boolean findStartPos(Level level) {
        while (true) {
            int x = this.random.nextInt(level.w);
            int y = this.random.nextInt(level.h);
            if (level.getTile(x, y) == Tile.grass) {
                this.x = x * 16 + 8;
                this.y = y * 16 + 8;
                return true;
            }
        }
    }

    public boolean payStamina(int cost) {
        if (cost > this.stamina) {
            return false;
        }
        this.stamina -= cost;
        return true;
    }

    public void changeLevel(int dir) {
        this.game.scheduleLevelChange(dir);
    }

    public int getLightRadius() {
        int r = 2;
        if (this.activeItem != null) {
            if (this.activeItem instanceof FurnitureItem) {
                int rr = ((FurnitureItem) this.activeItem).furniture.getLightRadius();
                if (rr > r) {
                    r = rr;
                }
            }
        }
        return r;
    }

    protected void die() {
        super.die();
        Sound.play(Sound.Event.DEATH, 1.0f);
    }

    protected void touchedBy(Entity entity) {
        if (!(entity instanceof Player)) {
            entity.touchedBy(this);
        }
    }

    protected void doHurt(int damage, int attackDir) {
        if (this.hurtTime > 0 || this.invulnerableTime > 0) {
            return;
        }

        Sound.play(Sound.Event.PLAYER_HURT, 1.0f);
        this.level.add(new TextParticle(String.valueOf(damage), this.x, this.y, 0xA83692));
        this.health -= damage;
        if (attackDir == 0) {
            this.yKnockback = +6;
        }
        if (attackDir == 1) {
            this.yKnockback = -6;
        }
        if (attackDir == 2) {
            this.xKnockback = -6;
        }
        if (attackDir == 3) {
            this.xKnockback = +6;
        }
        this.hurtTime = 10;
        this.invulnerableTime = 30;
    }

    public void gameWon() {
        this.level.player.invulnerableTime = 60 * 5;
        this.game.won();
    }
}
