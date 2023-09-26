package com.mojang.ld22.crafting;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.mojang.ld22.entity.Furniture;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.item.resource.Resource;
import me.kalmemarq.jgame.JsonHelper;
import me.kalmemarq.jgame.StringHelper;
import me.kalmemarq.jgame.logging.LogManager;
import me.kalmemarq.jgame.logging.Logger;

public class Crafting {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final Crafting INSTANCE;
    public final List<Recipe> anvilRecipes = new ArrayList<>();
    public final List<Recipe> ovenRecipes = new ArrayList<>();
    public final List<Recipe> furnaceRecipes = new ArrayList<>();
    public final List<Recipe> workbenchRecipes = new ArrayList<>();

    private Crafting() {
        try {
            this.loadRecipe("recipes/lantern.json");
            this.loadRecipe("recipes/oven.json");
            this.loadRecipe("recipes/furnace.json");
            this.loadRecipe("recipes/workbench.json");
            this.loadRecipe("recipes/chest.json");
            this.loadRecipe("recipes/anvil.json");

            this.loadRecipe("recipes/wooden_sword.json");
            this.loadRecipe("recipes/wooden_axe.json");
            this.loadRecipe("recipes/wooden_hoe.json");
            this.loadRecipe("recipes/wooden_pickaxe.json");
            this.loadRecipe("recipes/wooden_shovel.json");

            this.loadRecipe("recipes/rock_sword.json");
            this.loadRecipe("recipes/rock_axe.json");
            this.loadRecipe("recipes/rock_hoe.json");
            this.loadRecipe("recipes/rock_pickaxe.json");
            this.loadRecipe("recipes/rock_shovel.json");

            this.loadRecipe("recipes/iron_sword.json");
            this.loadRecipe("recipes/iron_axe.json");
            this.loadRecipe("recipes/iron_hoe.json");
            this.loadRecipe("recipes/iron_pickaxe.json");
            this.loadRecipe("recipes/iron_shovel.json");

            this.loadRecipe("recipes/golden_sword.json");
            this.loadRecipe("recipes/golden_axe.json");
            this.loadRecipe("recipes/golden_hoe.json");
            this.loadRecipe("recipes/golden_pickaxe.json");
            this.loadRecipe("recipes/golden_shovel.json");

            this.loadRecipe("recipes/gem_sword.json");
            this.loadRecipe("recipes/gem_axe.json");
            this.loadRecipe("recipes/gem_hoe.json");
            this.loadRecipe("recipes/gem_pickaxe.json");
            this.loadRecipe("recipes/gem_shovel.json");

            this.loadRecipe("recipes/iron_ingot.json");
            this.loadRecipe("recipes/gold_ingot.json");
            this.loadRecipe("recipes/glass.json");
            this.loadRecipe("recipes/bread.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadRecipe(String path) {
        String jsonContent = StringHelper.readString(Crafting.class.getResourceAsStream("/" + path));
        try {
            JsonNode json = JsonHelper.OBJECT_MAPPER.readTree(jsonContent);

            if (!JsonHelper.hasNonNullString(json, "type") || !JsonHelper.hasNonNullArray(json, "for") || !JsonHelper.hasNonNullObject(json, "result") || !JsonHelper.hasNonNullArray(json, "cost")) {
                return;
            }

            JsonNode resultObject = json.get("result");

            Recipe recipe;
            switch (json.get("type").textValue()) {
                case "furniture_recipe" -> {
                    Class<? extends Furniture> clazz = (Class<? extends Furniture>) Class.forName(resultObject.get("furniture").textValue());
                    recipe = new FurnitureRecipe(clazz);
                }
                case "tool_recipe" -> {
                    Field toolTypeField = ToolType.class.getField(resultObject.get("tool_type").textValue());
                    ToolType toolType = (ToolType) toolTypeField.get(null);
                    recipe = new ToolRecipe(toolType, resultObject.get("level").asInt(0));
                }
                case "resource_recipe" -> {
                    Field resourceField = Resource.class.getField(resultObject.get("resource").textValue());
                    Resource resource = (Resource) resourceField.get(null);
                    recipe = new ResourceRecipe(resource);
                }
                default -> {
                    return;
                }
            }

            JsonNode costArray = json.get("cost");
            for (JsonNode item : costArray) {
                if (!item.isObject() || !JsonHelper.hasNonNullString(item, "resource")) continue;
                int count = item.get("count").asInt(1);
                String resourceName = item.get("resource").textValue();
                Field resourceField = Resource.class.getField(resourceName);
                Resource resource = (Resource) resourceField.get(null);
                recipe.addCost(resource, count);
            }

            for (JsonNode item : json.get("for")) {
                LOGGER.info("Recipe '{}': {}", path, item.asText());
                switch (item.asText()) {
                    case "workbench" -> this.workbenchRecipes.add(recipe);
                    case "furnace" -> this.furnaceRecipes.add(recipe);
                    case "oven" -> this.ovenRecipes.add(recipe);
                    case "anvil" -> this.anvilRecipes.add(recipe);
                }
            }

            LOGGER.info("Loaded recipe '{}'", path);
        } catch (Exception e) {
            LOGGER.error("Failed to load recipe '{}'", path);
            LOGGER.error("Could not parse json", e);
        }
    }

    static {
        INSTANCE = new Crafting();
    }
}
