package com.mojang.ld22.entity;

import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StackedInventory {
    private final List<ItemStack> stacks = new ArrayList<>();

    public void add(ItemStack stack) {
        this.add(this.stacks.size(), stack);
    }

    public void add(int slot, ItemStack stack) {
        if (stack.isEmpty()) return;
        ItemStack similarStack = this.findSimilar(stack);

        if (similarStack == null || !stack.isStackable()) {
            this.stacks.add(slot, stack);
        } else {
            int maxAmount = stack.getItem().getMaxStackSize();
            int currentAmount = similarStack.getAmount();

            if (currentAmount + stack.getAmount() > maxAmount) {
                int leftAmount = currentAmount + stack.getAmount() - maxAmount;
                similarStack.setAmount(maxAmount);
                this.stacks.add(new ItemStack(stack.getItem(), leftAmount));
            } else {
                similarStack.increase(stack.getAmount());
            }
        }
    }

    private ItemStack findSimilar(ItemStack item) {
        for (ItemStack stack : this.stacks) {
            if (stack.canCombine(item)) {
                return stack;
            }
        }
        return null;
    }

    public boolean hasResources(ItemStack item, int count) {
        return this.count(item) >= count;
    }

    public boolean removeResource(ItemStack item, int count) {
        if (item.isEmpty()) return false;

        Iterator<ItemStack> iterator = this.stacks.iterator();
        int requestedCount = count;
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();

            if (!stack.isEmpty() && stack.canCombine(item)) {
                int left = stack.getAmount() - count;
                if (left > 0) {
                    stack.decrease(count);
                    count = 0;
                    break;
                } else if (left == 0) {
                    iterator.remove();
                    count = 0;
                } else {
                    count = -left;
                    iterator.remove();
                }
            }
        }

        return count != requestedCount;
    }

    public int count(ItemStack item) {
        int amount = 0;

        for (ItemStack stack : this.stacks) {
            if (!stack.isEmpty() && stack.canCombine(item)) {
                amount += stack.getAmount();
            }
        }

        return amount;
    }
}
