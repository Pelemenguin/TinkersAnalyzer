package pelemenguin.tinkersanalyzer.content;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;

public final class TinkersAnalyzerGraphs {

    public static void basicGraphData(CompoundTag original, int color) {
        original.putInt("color", color);
    }

    public static final ResourceLocation PLAIN_TEXT = TinkersAnalyzer.makeResource("plain_text");
    public static void plainTextGraphData(CompoundTag original, Component text, int maxWidth, int color) {
        original.putString("text", Component.Serializer.toJson(text));
        original.putInt("maxWidth", maxWidth);
        original.putInt("color", color);
    }
    public static void plainTextGraphData(CompoundTag original, Component title, Component text, int maxWidth, int color) {
        plainTextGraphData(original, text, maxWidth, color);
        original.putString("title", Component.Serializer.toJson(title));
    }

    public static final ResourceLocation DPS = TinkersAnalyzer.makeResource("dps");

    private TinkersAnalyzerGraphs() {}

}
