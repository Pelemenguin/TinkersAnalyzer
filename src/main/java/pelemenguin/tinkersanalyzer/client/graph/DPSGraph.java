package pelemenguin.tinkersanalyzer.client.graph;

import java.util.ArrayDeque;
import java.util.Deque;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import pelemenguin.tinkersanalyzer.client.graph.element.DiagramGraphElement;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerGraphs;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerModifiers;
import slimeknights.mantle.client.ResourceColorManager;

public class DPSGraph extends AnalyzerGraph {

    private static DPSGraph INSTANCE = null;
    public long timeOrigin = -1;

    public Deque<Vec2> recentDamages = new ArrayDeque<>();

    private DPSGraph(CompoundTag tag) {
        super(tag);
        this.addElement(new DiagramGraphElement(this, 64, 48)
                .horizontalAxisName(Component.literal("Time"))
                .verticalAxisName(Component.literal("Damage / DPS"))
                .labelHorizontalTick((t) -> "%.2gs".formatted(t / 20))
                .timeAsHorizontalAxis()
                .timeOrigin(() -> this.timeOrigin)
                .domain(-60.0f, 0.0f)
                .scatterDiagram(recentDamages)
                .autoYRange(0f, 2f, true, false)
            );
    }

    public static DPSGraph getInstance() {
        if (INSTANCE != null) return INSTANCE;
        CompoundTag tag = new CompoundTag();
        int color = ResourceColorManager.getColor(TinkersAnalyzerModifiers.DPS_ANALYZER.getId().toLanguageKey("modifier")) & 0xFFFFFF;
        TinkersAnalyzerGraphs.basicGraphData(tag, color);
        INSTANCE = new DPSGraph(tag);
        return INSTANCE;
    }

}
