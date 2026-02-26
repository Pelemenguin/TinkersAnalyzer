package pelemenguin.tinkersanalyzer.data;

import java.util.function.Consumer;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerItems;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerModifiers;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public final class TinkersAnalyzerRecipes extends RecipeProvider {

    public TinkersAnalyzerRecipes(PackOutput p_248933_) {
        super(p_248933_);
    }

    private static ResourceLocation modifierRecipeId(String path) {
        return TinkersAnalyzer.makeResource("modifiers/" + path);
    }

    private static ResourceLocation commonRecipeId(String path) {
        return TinkersAnalyzer.makeResource("common/" + path);
    }

    private static TagKey<Item> itemTagKey(String path) {
        return ForgeRegistries.ITEMS.tags().createTagKey(
                ResourceLocation.tryParse(path)
            );
    }

    private static Ingredient tagIngredient(String path) {
        return Ingredient.of(itemTagKey(path));
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {

        final Ingredient IRON_INGOTS = tagIngredient("forge:ingots/iron");
        final Ingredient AMETHYSTS = tagIngredient("forge:gems/amethyst");
        final Ingredient EMERALDS = tagIngredient("forge:gems/emerald");
        final Ingredient BLUE_DYES = tagIngredient("forge:dyes/blue");
        final Ingredient LIME_DYES = tagIngredient("forge:dyes/lime");
        final Ingredient CLOCK = Ingredient.of(Items.CLOCK);
        final Ingredient COMPASS = Ingredient.of(Items.COMPASS);

        // ================================ //
        // | Item Recipes                 | //
        // ================================ //

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkersAnalyzerItems.DPS_ANALYZER.get())
            .pattern("AIA")
            .pattern("DGD")
            .pattern("AIA")
            .define('A', AMETHYSTS)
            .define('I', IRON_INGOTS)
            .define('D', BLUE_DYES)
            .define('G', CLOCK)
            .unlockedBy("has_clock", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CLOCK))
            .save(consumer, commonRecipeId("dps_analyzer"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkersAnalyzerItems.ENTITY_RADAR.get())
            .pattern("EIE")
            .pattern("DCD")
            .pattern("EIE")
            .define('E', EMERALDS)
            .define('I', IRON_INGOTS)
            .define('D', LIME_DYES)
            .define('C', COMPASS)
            .unlockedBy("has_compass", InventoryChangeTrigger.TriggerInstance.hasItems(Items.COMPASS))
            .save(consumer, commonRecipeId("entity_radar"));

        // ================================ //
        // | Modifier Recipes             | //
        // ================================ //

        // DPS Analyzer
        ModifierRecipeBuilder.modifier(TinkersAnalyzerModifiers.DPS_ANALYZER)
            .addInput(TinkersAnalyzerItems.DPS_ANALYZER.get())
            .checkTraitLevel()
            .exactLevel(1)
            .setTools(TinkerTags.Items.MODIFIABLE)
            .save(consumer, modifierRecipeId("dps_analyzer"));

        ModifierRecipeBuilder.modifier(TinkersAnalyzerModifiers.ENTITY_RADAR)
        .addInput(TinkersAnalyzerItems.ENTITY_RADAR.get())
        .checkTraitLevel()
        .exactLevel(1)
        .setTools(TinkerTags.Items.MODIFIABLE)
        .save(consumer, modifierRecipeId("entity_radar"));

        // Copper Gauge
        ModifierRecipeBuilder.modifier(TinkersAnalyzerModifiers.COPPER_GAUGE)
            .addInput(TinkerSmeltery.copperGauge.get())
            .checkTraitLevel()
            .exactLevel(1)
            .setTools(TinkerTags.Items.MODIFIABLE)
            .save(consumer, modifierRecipeId("copper_gauge"));

        // Obsidian Gauge
        ModifierRecipeBuilder.modifier(TinkersAnalyzerModifiers.OBSIDIAN_GAUGE)
            .addInput(TinkerSmeltery.obsidianGauge.get())
            .checkTraitLevel()
            .exactLevel(1)
            .setTools(TinkerTags.Items.MODIFIABLE)
            .save(consumer, modifierRecipeId("obsidian_gauge"));

    }

}
