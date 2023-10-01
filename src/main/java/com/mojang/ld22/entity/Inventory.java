package com.mojang.ld22.entity;

import java.util.ArrayList;
import java.util.List;

import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.resource.Resource;

public class Inventory {
    public List<Item> items = new ArrayList<>();

    public void add(Item item) {
        this.add(this.items.size(), item);
    }

    public void add(int slot, Item item) {
        if (item instanceof ResourceItem toTake) {
            ResourceItem has = this.findResource(toTake.resource);
            if (has == null) {
                this.items.add(slot, toTake);
            } else {
                has.count += toTake.count;
            }
        } else {
            this.items.add(slot, item);
        }
    }

    private ResourceItem findResource(Resource resource) {
        for (Item item : this.items) {
            if (item instanceof ResourceItem has) {
                if (has.resource == resource) {
                    return has;
                }
            }
        }
        return null;
    }

    public boolean hasResources(Resource r, int count) {
        ResourceItem ri = this.findResource(r);
        if (ri == null) {
            return false;
        }
        return ri.count >= count;
    }

    public boolean removeResource(Resource r, int count) {
        ResourceItem ri = this.findResource(r);
        if (ri == null) {
            return false;
        }
        if (ri.count < count) {
            return false;
        }
        ri.count -= count;
        if (ri.count <= 0) {
            this.items.remove(ri);
        }
        return true;
    }

    public int count(Item item) {
        if (item instanceof ResourceItem) {
            ResourceItem ri = this.findResource(((ResourceItem) item).resource);
            if (ri != null) {
                return ri.count;
            }
        } else {
            int count = 0;
            for (Item value : this.items) {
                if (value.matches(item)) {
                    count++;
                }
            }
            return count;
        }
        return 0;
    }
}
