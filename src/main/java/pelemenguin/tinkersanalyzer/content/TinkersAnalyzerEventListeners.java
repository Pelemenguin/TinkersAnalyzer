package pelemenguin.tinkersanalyzer.content;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.client.AnalyzerOverlay;
import pelemenguin.tinkersanalyzer.content.network.TinkersAnalyzerNetwork;

@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = TinkersAnalyzer.MODID)
public final class TinkersAnalyzerEventListeners {

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof ServerPlayer player) {
            TinkersAnalyzerNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new EquipmentChangeMessage());
        }
    }

    public static class EquipmentChangeMessage {
        public EquipmentChangeMessage() {
        }

        public EquipmentChangeMessage(FriendlyByteBuf buf) {
        }

        public void encode(FriendlyByteBuf buf) {
        }

        public static void handle(EquipmentChangeMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
            NetworkEvent.Context ctx = ctxSupplier.get();
            ctx.enqueueWork(() -> {
                AnalyzerOverlay.INSTANCE.needUpdate();
            });
            ctx.setPacketHandled(true);
        }
    }

}
