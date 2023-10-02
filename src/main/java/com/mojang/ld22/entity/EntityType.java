package com.mojang.ld22.entity;

import me.kalmemarq.jgame.bso.BsoMap;

import java.util.HashMap;
import java.util.Map;

public class EntityType<T extends Entity> {
    public static final Map<String, EntityType<?>> REGISTRY = new HashMap<>();
    public static final Map<String, EntityType<?>> FURNITURE_REGISTRY = new HashMap<>();

    public static final EntityType<Furnace> FURNACE_FURNITURE = registerFurniture("furnace", new EntityType<>(Furnace::new));
    public static final EntityType<Lantern> LANTERN_FURNITURE = registerFurniture("lantern", new EntityType<>(Lantern::new));
    public static final EntityType<Oven> OVEN_FURNITURE = registerFurniture("oven", new EntityType<>(Oven::new));
    public static final EntityType<Spark> SPARK = register("spark", new EntityType<>(Spark::new));
    public static final EntityType<Workbench> WORKBENCH_FURNITURE = registerFurniture("workbench", new EntityType<>(Workbench::new));
    public static final EntityType<Chest> CHEST_FURNITURE = registerFurniture("chest", new EntityType<>(Chest::new));
    public static final EntityType<Anvil> ANVIL_FURNITURE = registerFurniture("anvil", new EntityType<>(Anvil::new));
    public static final EntityType<AirWizard> AIR_WIZARD = register("air_wizard", new EntityType<>(AirWizard::new));
    public static final EntityType<Zombie> ZOMBIE = register("zombie", new EntityType<>(Zombie::new));
    public static final EntityType<Slime> SLIME = register("slime", new EntityType<>(Slime::new));
    public static final EntityType<ItemEntity> ITEM_ENTITY = register("item_entity", new EntityType<>(ItemEntity::new));

    public static <T extends Entity> EntityType<T> register(String identifier, EntityType<T> entityType) {
        REGISTRY.put(identifier, entityType);
        return entityType;
    }

    public static <T extends Entity> EntityType<T> registerFurniture(String identifier, EntityType<T> entityType) {
        FURNITURE_REGISTRY.put(identifier, entityType);
        return register(identifier, entityType);
    }

    private final EntityFactory<T> factory;

    public EntityType(EntityFactory<T> factory) {
        this.factory = factory;
    }

    public T create() {
        return this.factory.create();
    }

    public T createWithData(BsoMap data) {
        T entity = this.factory.create();
        entity.writeData(data);
        return entity;
    }

    public interface EntityFactory<T extends Entity> {
        T create();
    }
}
