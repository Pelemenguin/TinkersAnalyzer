package pelemenguin.tinkersanalyzer.client.graph;

import java.util.ArrayDeque;
import java.util.Deque;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.client.graph.element.DiagramGraphElement;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerGraphs;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerModifiers;
import slimeknights.mantle.client.ResourceColorManager;

@EventBusSubscriber(modid = TinkersAnalyzer.MODID, bus = Bus.FORGE, value = Dist.CLIENT)
public class DPSGraph extends AnalyzerGraph {

    private static DPSGraph INSTANCE = null;

    Deque<Vec2> recentDamages = new ArrayDeque<>();

    private DPSGraph(CompoundTag tag) {
        super(tag);
        this.addElement(new DiagramGraphElement(this, 64, 48)
                .horizontalAxisName(Component.literal("Time"))
                .verticalAxisName(Component.literal("Damage / DPS"))
                .domain(-60.0f, 0.0f)
                .scatterDiagram(recentDamages)
                .timeAsHorizontalAxis()
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
    public static void onEntityHurt(LivingHurtEvent event) {
        Entity sourceEntity = event.getSource().getEntity();
        if (sourceEntity == null) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        if (sourceEntity.equals(player)) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;
            getInstance().recentDamages.addLast(new Vec2(level.getGameTime(), event.getAmount()));
        }
    }

}
