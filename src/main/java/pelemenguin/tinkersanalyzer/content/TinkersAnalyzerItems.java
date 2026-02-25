package pelemenguin.tinkersanalyzer.content;

import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.content.item.DPSAnalyzerItem;
import slimeknights.mantle.registration.deferred.ItemDeferredRegister;
import slimeknights.mantle.registration.object.ItemObject;

public final class TinkersAnalyzerItems {

    private static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(TinkersAnalyzer.MODID);

    public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final ItemObject<DPSAnalyzerItem> DPS_ANALYZER = ITEMS.register("dps_analyzer", () -> new DPSAnalyzerItem(new Item.Properties().stacksTo(1)));

    private TinkersAnalyzerItems() {}

}
