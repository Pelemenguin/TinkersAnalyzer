package pelemenguin.tinkersanalyzer.library;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class Analyzer {

    private HashMap<UUID, Pair<ResourceLocation, CompoundTag>> graphs = new HashMap<>();
    private HashMap<UUID, AnalyzerLayoutEntry> defaultLayouts = new HashMap<>();

    public Map<UUID, Pair<ResourceLocation, CompoundTag>> getAllGraphs() {
        return Map.copyOf(graphs);
    }

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
