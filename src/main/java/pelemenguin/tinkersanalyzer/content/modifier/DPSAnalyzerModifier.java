package pelemenguin.tinkersanalyzer.content.modifier;

import java.util.Deque;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.client.graph.DPSGraph;
import pelemenguin.tinkersanalyzer.client.graph.element.DiagramGraphElement.DataPoint;
import pelemenguin.tinkersanalyzer.client.graph.element.DiagramGraphElement.HistogramBar;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerItems;
import pelemenguin.tinkersanalyzer.content.network.TinkersAnalyzerNetwork;
import pelemenguin.tinkersanalyzer.library.Analyzer;
import pelemenguin.tinkersanalyzer.library.hook.DisplayAnalyzerGraphModifierHook;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

@EventBusSubscriber(modid = TinkersAnalyzer.MODID, bus = Bus.FORGE)
public class DPSAnalyzerModifier extends NoLevelsModifier implements DisplayAnalyzerGraphModifierHook {

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, DisplayAnalyzerGraphModifierHook.INSTANCE);
    }

    @Override
    public void addGraph(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, Analyzer analyzer) {
        TinkersAnalyzerItems.DPS_ANALYZER.get().addGraph(analyzer);
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        Entity sourceEntity = event.getSource().getEntity();
        if (sourceEntity == null) return;
        if (sourceEntity instanceof ServerPlayer player) {
            TinkersAnalyzerNetwork.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new DPSAnalyzerReceiveDamageMessage(player.level().getGameTime(), event.getAmount())
                );
        }
    }

    public static class DPSAnalyzerReceiveDamageMessage {
        private final float damage;
        private final long timestamp;

        public float getDamage() {
            return damage;
        }

        public float getTimestamp() {
            return timestamp;
        }

        public DPSAnalyzerReceiveDamageMessage(long timestamp, float damage) {
            this.timestamp = timestamp;
            this.damage = damage;
        }

        public DPSAnalyzerReceiveDamageMessage(FriendlyByteBuf buf) {
            this.timestamp = buf.readLong();
            this.damage = buf.readFloat();
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeLong(this.timestamp);
            buf.writeFloat(this.damage);
        }

        public static void handle(DPSAnalyzerReceiveDamageMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
            NetworkEvent.Context ctx = ctxSupplier.get();
            ctx.enqueueWork(() -> {
                ClientLevel level = Minecraft.getInstance().level;
                if (level == null) return;
                long gameTime = level.getGameTime();
                DPSGraph dpsGraph = DPSGraph.getInstance();
                long diff = gameTime - dpsGraph.timeOrigin;
                if (dpsGraph.timeOrigin < 0 || diff > 18000) {
                    dpsGraph.timeOrigin = gameTime;

                    // Update time origin
                    Deque<HistogramBar> recentDamages = dpsGraph.recentDamages;
                    int pointCount = recentDamages.size();
                    for (int i = 0; i < pointCount; i++) {
                        HistogramBar original = recentDamages.pollFirst();
                        recentDamages.addLast(new HistogramBar(original.leftX() - diff, original.rightX() - diff, original.y()));
                    }
                    Deque<DataPoint> dps = dpsGraph.dps;
                    int dpsCount = dps.size();
                    for (int i = 0; i < dpsCount; i++) {
                        DataPoint original = dps.pollFirst();
                        dps.addLast(new DataPoint(original.x() - diff, original.y()));
                    }
                    Deque<DataPoint> averageDps = dpsGraph.averageDps;
                    int averageDpsCount = averageDps.size();
                    for (int i = 0; i < averageDpsCount; i++) {
                        DataPoint original = averageDps.pollFirst();
                        averageDps.addLast(new DataPoint(original.x() - diff, original.y()));
                    }
                }
                float relative = gameTime - dpsGraph.timeOrigin;
                dpsGraph.recentDamages.addLast(new HistogramBar(relative - 2, relative + 2, message.getDamage()));
            });
            ctx.setPacketHandled(true);
        }
    }

}
