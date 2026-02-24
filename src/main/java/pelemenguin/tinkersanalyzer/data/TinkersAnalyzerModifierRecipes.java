package pelemenguin.tinkersanalyzer.data;

import java.util.function.Consumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerModifiers;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

@Mod.EventBusSubscriber(bus = Bus.MOD, modid = TinkersAnalyzer.MODID)
public final class TinkersAnalyzerModifierRecipes extends RecipeProvider {

    public TinkersAnalyzerModifierRecipes(PackOutput p_248933_) {
        super(p_248933_);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeServer(), new TinkersAnalyzerModifierRecipes(generator.getPackOutput()));
    }

    private static ResourceLocation recipeId(String path) {
        return TinkersAnalyzer.makeResource("modifiers/" + path);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {

        // Copper Gauge
        ModifierRecipeBuilder.modifier(TinkersAnalyzerModifiers.COPPER_GAUGE)
            .addInput(TinkerSmeltery.copperGauge.asItem())
            .checkTraitLevel()
            .exactLevel(1)
            .setTools(TinkerTags.Items.MODIFIABLE)
            .save(consumer, recipeId("copper_gauge"));

        // Obsidian Gauge
        ModifierRecipeBuilder.modifier(TinkersAnalyzerModifiers.OBSIDIAN_GAUGE)
            .addInput(TinkerSmeltery.obsidianGauge.asItem())
            .checkTraitLevel()
            .exactLevel(1)
            .setTools(TinkerTags.Items.MODIFIABLE)
            .save(consumer, recipeId("obsidian_gauge"));

    }

}
