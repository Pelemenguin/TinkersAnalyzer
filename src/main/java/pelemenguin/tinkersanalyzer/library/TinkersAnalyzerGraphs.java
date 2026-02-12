package pelemenguin.tinkersanalyzer.library;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;

public final class TinkersAnalyzerGraphs {

    public static final ResourceLocation PLAIN_TEXT = TinkersAnalyzer.makeResource("plain_text");
    public static void plainTextGraphData(CompoundTag original, Component text, int maxWidth, int color) {
        original.putString("text", Component.Serializer.toJson(text));
        original.putInt("maxWidth", maxWidth);
        original.putInt("color", color);
    }

    private TinkersAnalyzerGraphs() {}

}
