package pelemenguin.tinkersanalyzer.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.Type;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.content.client.TinkersAnalyzerBuiltinGraphs;

@Mod.EventBusSubscriber(modid = TinkersAnalyzer.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public final class TinkersAnalyzerClient {

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelow(VanillaGuiOverlay.HOTBAR.id(), "tinkers_analyzer", AnalyzerOverlay.INSTANCE);
    }

    public static void init() {
        TinkersAnalyzerBuiltinGraphs.init();
        AnalyzerLayoutConfig.init();
        ModLoadingContext.get().registerConfig(Type.CLIENT, AnalyzerLayoutConfig.spec());
    }

}
