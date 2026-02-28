package pelemenguin.tinkersanalyzer.content;

import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.content.item.EntityRadarItem;
import pelemenguin.tinkersanalyzer.content.item.SimpleAnalyzerItem;
import pelemenguin.tinkersanalyzer.library.AnalyzerLayoutEntry;
import slimeknights.mantle.registration.deferred.ItemDeferredRegister;
import slimeknights.mantle.registration.object.ItemObject;

public final class TinkersAnalyzerItems {

    private static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(TinkersAnalyzer.MODID);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TinkersAnalyzer.MODID);

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        CREATIVE_TABS.register(bus);
    }

    public static final RegistryObject<CreativeModeTab> creativeModeTab = CREATIVE_TABS.register("default", CreativeModeTab.builder()
            .icon(TinkersAnalyzerItems::icon)
            .title(TinkersAnalyzer.makeTranslation("itemGroup", "default"))
            .displayItems(TinkersAnalyzerItems::addTabItems)
            ::build);

    public static final ItemObject<SimpleAnalyzerItem> DPS_ANALYZER = ITEMS.register("dps_analyzer", () -> new SimpleAnalyzerItem(new Item.Properties().stacksTo(1),
            UUID.fromString("291b4f42-6559-4bda-be8f-508329b7cffc"),
            TinkersAnalyzerGraphs.DPS,
            new AnalyzerLayoutEntry(30.0f, -10.0f, 192.0f),
            TinkersAnalyzer.makeTranslation("item", "dps_analyzer.tooltip").withStyle(ChatFormatting.GRAY)
        ));
    public static final ItemObject<EntityRadarItem> ENTITY_RADAR = ITEMS.register("entity_radar", EntityRadarItem::new);

    private static ItemStack icon() {
        return ENTITY_RADAR.get().getDefaultInstance();
    }

    private static void addTabItems(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.accept(DPS_ANALYZER);
        output.accept(ENTITY_RADAR);
    }

    private TinkersAnalyzerItems() {}

}
