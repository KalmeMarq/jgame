package com.mojang.ld22.crafting;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.mojang.ld22.Game;
import com.mojang.ld22.entity.Furniture;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.item.resource.Resource;
import me.kalmemarq.jgame.resource.PackResource;
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
        for (PackResource resource : Game.getInstance().vanillaResourcePack.getFiles("recipes", name -> name.endsWith(".json"))) {
            this.loadRecipe(resource);
        }
        LOGGER.info("Recipes: Expected={} Found={}", 35, this.anvilRecipes.size() + this.ovenRecipes.size() + this.furnaceRecipes.size() + this.workbenchRecipes.size());
    }

    @SuppressWarnings("unchecked")
    private void loadRecipe(PackResource file) {
        String jsonContent;
        try {
            jsonContent =  StringHelper.readString(file.getInputStream());
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
                switch (item.asText()) {
                    case "workbench" -> this.workbenchRecipes.add(recipe);
                    case "furnace" -> this.furnaceRecipes.add(recipe);
                    case "oven" -> this.ovenRecipes.add(recipe);
                    case "anvil" -> this.anvilRecipes.add(recipe);
                }
            }

            LOGGER.info("Loaded recipe '{}'", file.getPath());
        } catch (Exception e) {
            LOGGER.error("Failed to load recipe '{}'", file.getPath());
            LOGGER.error("Could not parse json", e);
        }
    }

    static {
        INSTANCE = new Crafting();
    }
}
