package com.mojang.ld22.item;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;
import me.kalmemarq.jgame.bso.BsoMap;

import java.util.Objects;

public class ItemStack {
    private static final ItemStack EMPTY = new ItemStack(null, 0);

    private final Item item;
    private int amount;
    private BsoMap data;

    public ItemStack(Item item) {
        this(item, 1);
    }

    public ItemStack(Item item, int amount) {
        this.item = item;
        this.setAmount(amount);
    }

    public void setAmount(int amount) {
        this.amount = amount;
        if (this.amount < 0) {
            this.amount = 0;
        }
        if (this.isEmpty()) {
            this.amount = 0;
        } else {
            if (this.amount > this.item.getMaxStackSize()) {
                this.amount = this.item.getMaxStackSize();
            }
        }
    }

    public void increase(int amount) {
       this.setAmount(this.amount + amount);
    }

    public void decrease(int amount) {
        this.increase(-amount);
    }

    public Item getItem() {
        return this.item;
    }

    public int getAmount() {
        return this.amount;
    }

    public boolean isEmpty() {
        return this == ItemStack.EMPTY || this.item == null || this.amount <= 0;
    }

    public boolean isStackable() {
        return !this.isEmpty() && this.item.getMaxStackSize() > 1;
    }

    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
        if (!this.isEmpty()) {
            return this.item.interactOn(tile, level, xt, yt, player, attackDir);
        }
        return false;
    }

    public boolean isOf(Item item) {
        return this.getItem() == item;
    }

    public boolean canCombine(ItemStack other) {
        if (this.isEmpty() && other.isEmpty()) return true;
        return this.isOf(other.getItem()) && Objects.equals(this.data, other.data);
    }

    public boolean hasData() {
        return !this.isEmpty() && this.data != null && !this.data.isEmpty();
    }

    public BsoMap getData() {
        return this.data;
    }

    public BsoMap getOrCreateData() {
        if (this.data == null) {
            this.data = new BsoMap();
        }
        return this.data;
    }

    public void setData(BsoMap data) {
        this.data = data;
    }
}
