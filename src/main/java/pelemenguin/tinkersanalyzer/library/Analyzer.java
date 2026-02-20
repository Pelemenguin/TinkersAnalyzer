package pelemenguin.tinkersanalyzer.library;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

/**
 * A class for collecting {@link pelemenguin.tinkersanalyzer.client.graph.AnalyzerGraph AnalyzerGraph}s to be added to the screen.
 */
public class Analyzer {

    private HashMap<UUID, Pair<ResourceLocation, CompoundTag>> graphs = new HashMap<>();
    private HashMap<UUID, AnalyzerLayoutEntry> defaultLayouts = new HashMap<>();

    /**
     * Gets a copy of the map that stored all collected graphs.
     * 
     * @return The collected graphs
     */
    public Map<UUID, Pair<ResourceLocation, CompoundTag>> getAllGraphs() {
        return Map.copyOf(graphs);
    }

    /**
     * Create or modify the data of a {@link pelemenguin.tinkersanalyzer.client.graph.AnalyzerGraph AnalyzerGraph}.
     *
     * <p>
     * If a {@link java.util.UUID UUID} does not have an AnalyzerGraph added,
     * a new {@link net.minecraft.nbt.CompoundTag CompoundTag} will be created and returned.
     * Add the necessary data in this CompoundTag to provide information for creating the graph.
     *
     * <p>
     * If an added AnalyzerGraph is already using this UUID, the previously stored CompoundTag for that graph will be returned.
     * You can continue to modify the data in this CompoundTag.
     *
     * <p>
     * Different types of AnalyzerGraphs require different data.
     * For the AnalyzerGraph base class, it requires an integer property {@code color} to determine the background color.
     * Subclasses of other AnalyzerGraphs may need different data and should provide it as needed.
     *
     * @param uuid          The UUID of the graph
     * @param graphId       A {@link net.minecraft.resources.ResourceLocation ResourceLocation} representing the type of AnalyzerGraph.
     *                      It should be registered in {@link pelemenguin.tinkersanalyzer.client.AnalyzerOverlay AnalyzerOverlay} in advance
     * @param defaultLayout The default layout of the graph
     * @return              A CompoundTag representing the data of the AnalyzerGraph
     */
    public CompoundTag createOrGetGraphData(UUID uuid, ResourceLocation graphId, AnalyzerLayoutEntry defaultLayout) {
        Pair<ResourceLocation, CompoundTag> pair = graphs.get(uuid);
        CompoundTag data;
        if (pair == null) {
            data = new CompoundTag();
            pair = Pair.of(graphId, data);
            graphs.put(uuid, pair);
        } else {
            data = pair.getSecond();
        }
        this.defaultLayouts.put(uuid, defaultLayout);
        return data;
    }

    /**
     * An overload of createOrGetGraphData without parameter {@code defaultLayout}.
     * 
     * @see #createOrGetGraphData(UUID, ResourceLocation, AnalyzerLayoutEntry)
     */
    public CompoundTag createOrGetGraphData(UUID uuid, ResourceLocation graphId) {
        return this.createOrGetGraphData(uuid, graphId, new AnalyzerLayoutEntry(0.0f, 0.0f, 192.0f));
    }

    @Nullable
    public AnalyzerLayoutEntry getDefaultLayoutFor(UUID uuid) {
        return this.defaultLayouts.get(uuid);
    }

    public boolean isEmpty() {
        return this.graphs.isEmpty();
    }

}
