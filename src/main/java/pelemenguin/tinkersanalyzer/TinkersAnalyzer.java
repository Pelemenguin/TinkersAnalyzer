package pelemenguin.tinkersanalyzer;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import pelemenguin.tinkersanalyzer.client.TinkersAnalyzerClient;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerItems;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerModifiers;

@Mod(TinkersAnalyzer.MODID)
public final class TinkersAnalyzer {
    public static final String MODID = "tinkers_analyzer";

    public TinkersAnalyzer() {
        TinkersAnalyzerItems.init();
        TinkersAnalyzerModifiers.init();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> TinkersAnalyzerClient::init);
    }

    public static final ResourceLocation makeResource(String path) {
        return new ResourceLocation(MODID, path);
    }
}
