package pelemenguin.tinkersanalyzer.client.graph;

import net.minecraft.nbt.CompoundTag;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.client.graph.element.RadarGraphElement;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerGraphs;
import slimeknights.mantle.client.ResourceColorManager;

public class EntityRadarGraph extends AnalyzerGraph {

    private static EntityRadarGraph INSTANCE;

    private EntityRadarGraph(CompoundTag tag) {
        super(tag);

        this.addElement(new RadarGraphElement(15).color(this.color));
    }

    public static EntityRadarGraph getInstance() {
        if (INSTANCE == null) {
            CompoundTag arg = new CompoundTag();
            TinkersAnalyzerGraphs.basicGraphData(arg, 0x00FFFFFF & ResourceColorManager.getColor(TinkersAnalyzer.makeModifierTranslationKey("entity_radar")));
            INSTANCE = new EntityRadarGraph(arg);
        }
        return INSTANCE;
    }

}
