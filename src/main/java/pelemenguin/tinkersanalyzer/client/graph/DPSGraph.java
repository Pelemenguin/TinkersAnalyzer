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
import pelemenguin.tinkersanalyzer.client.graph.element.TaggedTextGraphElement;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerGraphs;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerModifiers;
import slimeknights.mantle.client.ResourceColorManager;

@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = TinkersAnalyzer.MODID, value = Dist.CLIENT)
public class DPSGraph extends AnalyzerGraph {

    private static final int TIME_RANGE = 100;
    private static final int DPS_COLOR = 0xF7005A;
    private static final int AVERAGE_DPS_COLOR = 0xFFD238;

    private static DPSGraph INSTANCE = null;
    public long timeOrigin = -1;

    public Deque<DiagramGraphElement.HistogramBar> recentDamages = new ArrayDeque<>();
    public Deque<DiagramGraphElement.DataPoint> dps = new ArrayDeque<>();
    private float[] cachedDps = new float[TIME_RANGE];
    private float cachedAverageDps = 0.0f;
    public Deque<DiagramGraphElement.DataPoint> averageDps = new ArrayDeque<>();

    private float lastDps = 0.0f;
    private float lastAverageDps = 0.0f;

    private DPSGraph(CompoundTag tag) {
        super(tag);
        this.addElement(new DiagramGraphElement(this, 64, 48)
                .horizontalAxisName(Component.literal("Time"))
                .verticalAxisName(Component.literal("Damage / DPS"))
                .labelHorizontalTick((t) -> "%.1f".formatted(t / 20.0f) + "s")
                .timeAsHorizontalAxis()
                .timeOrigin(() -> this.timeOrigin)
                .domain(-TIME_RANGE, 0.0f)
                .histogram(this.recentDamages)
                .lineGraph(this.dps)
                .colorLastDiagram(DPS_COLOR)
                .lineGraph(this.averageDps)
                .colorLastDiagram(AVERAGE_DPS_COLOR)
                .autoYRange(0f, 2f, true, false)
            );

        TaggedTextGraphElement lastDamage = new TaggedTextGraphElement(() -> Component.literal(
                this.recentDamages.isEmpty() ? "-" : DiagramGraphElement.DEFAULT_LABEL_FORMAT.getTickLabel(this.recentDamages.peekLast().y())
            ))
            .colored(this.getColor())
            .tagBelow(Component.literal("LD"));
        lastDamage.x = 68;
        lastDamage.y = 0;
        lastDamage.scale = 2.0f;
        this.addElement(lastDamage);

        TaggedTextGraphElement curDps = new TaggedTextGraphElement(() -> Component.literal(DiagramGraphElement.DEFAULT_LABEL_FORMAT.getTickLabel(this.lastDps)))
            .colored(DPS_COLOR)
            .tagBelow(Component.literal("DPS"));
        curDps.x = 68;
        curDps.y = 16;
        curDps.scale = 2.0f;
        this.addElement(curDps);

        TaggedTextGraphElement averageDps = new TaggedTextGraphElement(() -> Component.literal(DiagramGraphElement.DEFAULT_LABEL_FORMAT.getTickLabel(this.lastAverageDps)))
            .colored(AVERAGE_DPS_COLOR)
            .tagBelow(Component.literal("Avg. DPS"));
        averageDps.x = 68;
        averageDps.y = 32;
        averageDps.scale = 2.0f;
        this.addElement(averageDps);
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
            float weight = x + TIME_RANGE;
            weight *= weight;
            sum += bar.y() * weight;
        }
        int x = (int) (gameTime - instance.timeOrigin);
        float dps = sum * 60.0f / (TIME_RANGE * TIME_RANGE * TIME_RANGE);
        instance.dps.addLast(new DiagramGraphElement.DataPoint(x, dps));

        // Force recalculate
        if (gameTime % TIME_RANGE == 0) {
            sum = 0;
            instance.cachedDps[(int) (gameTime % TIME_RANGE)] = dps;
            for (float f : instance.cachedDps) {
                sum += f;
            }
            instance.cachedAverageDps = sum / TIME_RANGE;
        } else {
            float diff = dps - instance.cachedDps[(int) (gameTime % TIME_RANGE)];
            instance.cachedDps[(int) (gameTime % TIME_RANGE)] = dps;
            instance.cachedAverageDps += diff / TIME_RANGE;
        }
        instance.averageDps.addLast(new DiagramGraphElement.DataPoint(x, instance.cachedAverageDps));

        if (gameTime % 10 == 0) {
            if (!instance.dps.isEmpty()) {
                instance.lastDps = instance.dps.peekLast().y();
            }
            if (!instance.averageDps.isEmpty()) {
                instance.lastAverageDps = instance.averageDps.peekLast().y();
            }
        }
    }

}
