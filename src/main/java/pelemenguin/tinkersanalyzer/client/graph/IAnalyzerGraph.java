package pelemenguin.tinkersanalyzer.client.graph;

import net.minecraft.nbt.CompoundTag;

@FunctionalInterface
public interface IAnalyzerGraph {
    AnalyzerGraph createGraph(CompoundTag data);
}
