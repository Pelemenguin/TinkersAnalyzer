package pelemenguin.tinkersanalyzer.client.graph;

import java.util.ArrayDeque;
import java.util.Deque;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.client.graph.element.DiagramGraphElement;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerGraphs;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerModifiers;
import slimeknights.mantle.client.ResourceColorManager;

@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = TinkersAnalyzer.MODID, value = Dist.CLIENT)
public class DPSGraph extends AnalyzerGraph {

    private static final int TIME_RANGE = 100;

    private static DPSGraph INSTANCE = null;
    public long timeOrigin = -1;

    public Deque<DiagramGraphElement.HistogramBar> recentDamages = new ArrayDeque<>();
    public float[] dpsArray = new float[TIME_RANGE];
    public Deque<DiagramGraphElement.DataPoint> averageDps = new ArrayDeque<>();

    private float cachedAverageDps = 0.0f;

    private DPSGraph(CompoundTag tag) {
        super(tag);
        this.addElement(new DiagramGraphElement(this, 64, 48)
                .horizontalAxisName(Component.literal("Time"))
                .verticalAxisName(Component.literal("Damage / DPS"))
                .labelHorizontalTick((t) -> "%.2gs".formatted(t / 20))
                .timeAsHorizontalAxis()
                .timeOrigin(() -> this.timeOrigin)
                .domain(-TIME_RANGE, 0.0f)
                .histogram(this.recentDamages)
                .lineGraph(this.averageDps)
                .colorLastDiagram(0xF7005A)
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

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        DPSGraph instance = getInstance();
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        long gameTime = level.getGameTime();
        float sum = 0;
        for (DiagramGraphElement.HistogramBar bar : instance.recentDamages) {
            float x = bar.middleX() + instance.timeOrigin - gameTime;
            if (x < - TIME_RANGE) continue;
            if (x > 0) continue;
            sum += bar.y();
        }
        float originalDps = instance.dpsArray[(int) (gameTime % TIME_RANGE)];
        float curDps = sum * 20.0f / TIME_RANGE;
        if (gameTime % 18000 == 0) {
            // Recalculate
            sum = 0;
            for (float f : instance.dpsArray) {
                sum += f;
            }
            instance.cachedAverageDps = sum / TIME_RANGE;
        } else {
            instance.cachedAverageDps += (curDps - originalDps) / TIME_RANGE;
        }
        instance.dpsArray[(int) (gameTime % TIME_RANGE)] = curDps;
        instance.averageDps.addLast(new DiagramGraphElement.DataPoint(gameTime - instance.timeOrigin, instance.cachedAverageDps));
    }

}
