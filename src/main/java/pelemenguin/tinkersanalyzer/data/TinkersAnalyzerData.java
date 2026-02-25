package pelemenguin.tinkersanalyzer.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;

@Mod.EventBusSubscriber(modid = TinkersAnalyzer.MODID, bus = Bus.MOD)
public class TinkersAnalyzerData {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        boolean server = event.includeServer();
        generator.addProvider(server, new TinkersAnalyzerRecipes(packOutput));

        boolean client = event.includeClient();
        generator.addProvider(client, new TinkersAnalyzerItemModels(packOutput, existingFileHelper));
    }

}
