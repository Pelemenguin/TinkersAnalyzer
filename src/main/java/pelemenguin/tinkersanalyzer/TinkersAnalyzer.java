package pelemenguin.tinkersanalyzer;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import pelemenguin.tinkersanalyzer.client.TinkersAnalyzerClient;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerItems;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerModifiers;
import pelemenguin.tinkersanalyzer.content.network.TinkersAnalyzerNetwork;

/**
 * The main mod class for Tinker's Analyzer
 */
@Mod(TinkersAnalyzer.MODID)
public final class TinkersAnalyzer {
    /**
     * The mod id.
     */
    public static final String MODID = "tinkers_analyzer";

    /**
     * Initializes the mod. Called by Forge.
     */
    public TinkersAnalyzer() {
        TinkersAnalyzerItems.init();
        TinkersAnalyzerModifiers.init();
        TinkersAnalyzerNetwork.init();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> TinkersAnalyzerClient::init);
    }

    /**
     * Make a {@link ResourceLocation}.
     * @param path The path of the {@code ResourceLocation}
     * @return     {@code new ResourceLocation(MODID, path)}
     */
    public static final ResourceLocation makeResource(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static final String makeTranslationKey(String category, String id) {
        return "%s.%s.%s".formatted(category, MODID, id);
    }

    public static final String makeAnalyzerTranslationKey(String id) {
        return makeTranslationKey("analyzer", id);
    }

    public static final String makeModifierTranslationKey(String id) {
        return makeTranslationKey("modifier", id);
    }

    public static final MutableComponent makeTranslation(String category, String id) {
        return Component.translatable(makeTranslationKey(category, id));
    }

    public static final MutableComponent makeAnalyzerTranslation(String id) {
        return Component.translatable(makeAnalyzerTranslationKey(id));
    }

    public static final MutableComponent makeModifierTranslation(String id) {
        return Component.translatable(makeModifierTranslationKey(id));
    }

}
