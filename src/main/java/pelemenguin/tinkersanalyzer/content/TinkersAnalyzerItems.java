package pelemenguin.tinkersanalyzer.content;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import slimeknights.mantle.registration.deferred.ItemDeferredRegister;
import slimeknights.mantle.registration.object.ItemObject;

public final class TinkersAnalyzerItems {

    private static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(TinkersAnalyzer.MODID);

    public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final ItemObject<Item> TINKERS_ANALYZER = ITEMS.register("tinkers_analyzer", new Properties().stacksTo(1));

    private TinkersAnalyzerItems() {}

}
