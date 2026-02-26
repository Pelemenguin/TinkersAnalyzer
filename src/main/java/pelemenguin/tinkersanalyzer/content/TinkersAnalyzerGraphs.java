package pelemenguin.tinkersanalyzer.content;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.content.modifier.FluidGaugeModifier.GaugeType;

public final class TinkersAnalyzerGraphs {

    public static void basicGraphData(CompoundTag original, int color) {
        original.putInt("color", color);
    }

    public static final ResourceLocation PLAIN_TEXT = TinkersAnalyzer.makeResource("plain_text");
    public static final ResourceLocation DPS = TinkersAnalyzer.makeResource("dps");

    public static void plainTextGraphData(CompoundTag original, Component text, int maxWidth, int color) {
        original.putString("text", Component.Serializer.toJson(text));
        original.putInt("maxWidth", maxWidth);
        original.putInt("color", color);
    }
    public static void plainTextGraphData(CompoundTag original, Component title, Component text, int maxWidth, int color) {
        plainTextGraphData(original, text, maxWidth, color);
        original.putString("title", Component.Serializer.toJson(title));
    }

    public static final ResourceLocation FLUID_GAUGE = TinkersAnalyzer.makeResource("fluid_gauge");
    public static void fluidGaugeGraphData(CompoundTag original, EquipmentSlot slot, GaugeType type) {
        CompoundTag tag = original.getCompound("slots");
        tag.putString(slot.toString(), type.toString());
        original.put("slots", tag);
    }

    public static final ResourceLocation ENTITY_RADAR = TinkersAnalyzer.makeResource("entity_radar");

    private TinkersAnalyzerGraphs() {}

}
