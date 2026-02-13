package pelemenguin.tinkersanalyzer.client.graph;

import net.minecraft.nbt.CompoundTag;

@FunctionalInterface
public interface IAnalyzerGraphCreator {
    AnalyzerGraph createGraph(CompoundTag data);
}
