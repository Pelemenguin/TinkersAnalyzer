package pelemenguin.tinkersanalyzer.content;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.content.modifier.DPSAnalyzerModifier;
import pelemenguin.tinkersanalyzer.content.modifier.EntityRadarModifier;
import pelemenguin.tinkersanalyzer.content.modifier.FluidGaugeModifier;
import pelemenguin.tinkersanalyzer.content.modifier.FluidGaugeModifier.GaugeType;
import pelemenguin.tinkersanalyzer.content.modifier.TinkersAnalyzerModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

public final class TinkersAnalyzerModifiers {

    public static ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(TinkersAnalyzer.MODID);

    public static final StaticModifier<TinkersAnalyzerModifier> TINKERS_ANALYZER = MODIFIERS.register("tinkers_analyzer", TinkersAnalyzerModifier::new);
    public static final StaticModifier<DPSAnalyzerModifier> DPS_ANALYZER = MODIFIERS.register("dps_analyzer", DPSAnalyzerModifier::new);
    public static final StaticModifier<FluidGaugeModifier> COPPER_GAUGE = MODIFIERS.register("copper_gauge", () -> new FluidGaugeModifier(GaugeType.COPPER));
    public static final StaticModifier<FluidGaugeModifier> OBSIDIAN_GAUGE = MODIFIERS.register("obsidian_gauge", () -> new FluidGaugeModifier(GaugeType.OBSIDIAN));
    public static final StaticModifier<EntityRadarModifier> ENTITY_RADAR = MODIFIERS.register("entity_radar", EntityRadarModifier::new);

    public static void init() {
        MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private TinkersAnalyzerModifiers() {}

}
