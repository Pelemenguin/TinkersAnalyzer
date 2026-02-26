package pelemenguin.tinkersanalyzer.content;

import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.content.item.SimpleAnalyzerItem;
import pelemenguin.tinkersanalyzer.library.AnalyzerLayoutEntry;
import slimeknights.mantle.registration.deferred.ItemDeferredRegister;
import slimeknights.mantle.registration.object.ItemObject;

public final class TinkersAnalyzerItems {

    private static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(TinkersAnalyzer.MODID);

    public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final ItemObject<SimpleAnalyzerItem> DPS_ANALYZER = ITEMS.register("dps_analyzer", () -> new SimpleAnalyzerItem(new Item.Properties().stacksTo(1),
            UUID.fromString("291b4f42-6559-4bda-be8f-508329b7cffc"),
            TinkersAnalyzerGraphs.DPS,
            new AnalyzerLayoutEntry(30.0f, -10.0f, 192.0f),
            TinkersAnalyzer.makeTranslation("item", "dps_analyzer.tooltip").withStyle(ChatFormatting.GRAY)
        ));
    public static final ItemObject<SimpleAnalyzerItem> ENTITY_RADAR = ITEMS.register("entity_radar", () -> new SimpleAnalyzerItem(new Item.Properties().stacksTo(1),
            UUID.fromString("d009369d-61ed-4228-b5b7-a32d118945a4"),
            TinkersAnalyzerGraphs.ENTITY_RADAR,
            new AnalyzerLayoutEntry(15, -10, 128),
            TinkersAnalyzer.makeTranslation("item", "entity_radar.tooltip.1").withStyle(ChatFormatting.GRAY),
            TinkersAnalyzer.makeTranslation("item", "entity_radar.tooltip.2").withStyle(ChatFormatting.GRAY),
            TinkersAnalyzer.makeTranslation("item", "entity_radar.tooltip.3").withStyle(ChatFormatting.GRAY)
        ));

    private TinkersAnalyzerItems() {}

}
