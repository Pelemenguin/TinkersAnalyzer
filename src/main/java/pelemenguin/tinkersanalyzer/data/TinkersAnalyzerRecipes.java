package pelemenguin.tinkersanalyzer.data;

import java.util.function.Consumer;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
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

    private static ResourceLocation recipeId(String path) {
        return TinkersAnalyzer.makeResource("modifiers/" + path);
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
        final Ingredient BLUE_DYES = tagIngredient("forge:dyes/blue");
        final Ingredient CLOCK = Ingredient.of(Items.CLOCK);

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
            .unlockedBy("has_amethyst", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(itemTagKey("forge:gems/amethyst")).build()
                ))
            .save(consumer);

        // ================================ //
        // | Modifier Recipes             | //
        // ================================ //

        // DPS Analyzer
        ModifierRecipeBuilder.modifier(TinkersAnalyzerModifiers.DPS_ANALYZER)
            .addInput(TinkersAnalyzerItems.DPS_ANALYZER.get())
            .checkTraitLevel()
            .exactLevel(1)
            .setTools(TinkerTags.Items.MODIFIABLE)
            .save(consumer, recipeId("dps_analyzer"));

        // Copper Gauge
        ModifierRecipeBuilder.modifier(TinkersAnalyzerModifiers.COPPER_GAUGE)
            .addInput(TinkerSmeltery.copperGauge.get())
            .checkTraitLevel()
            .exactLevel(1)
            .setTools(TinkerTags.Items.MODIFIABLE)
            .save(consumer, recipeId("copper_gauge"));

        // Obsidian Gauge
        ModifierRecipeBuilder.modifier(TinkersAnalyzerModifiers.OBSIDIAN_GAUGE)
            .addInput(TinkerSmeltery.obsidianGauge.get())
            .checkTraitLevel()
            .exactLevel(1)
            .setTools(TinkerTags.Items.MODIFIABLE)
            .save(consumer, recipeId("obsidian_gauge"));

    }

}
