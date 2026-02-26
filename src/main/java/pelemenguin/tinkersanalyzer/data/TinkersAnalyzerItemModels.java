package pelemenguin.tinkersanalyzer.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerItems;

public class TinkersAnalyzerItemModels extends ItemModelProvider {

    public TinkersAnalyzerItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TinkersAnalyzer.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.basicItem(TinkersAnalyzerItems.DPS_ANALYZER.get());
        this.basicItem(TinkersAnalyzerItems.ENTITY_RADAR.get());
    }

}
