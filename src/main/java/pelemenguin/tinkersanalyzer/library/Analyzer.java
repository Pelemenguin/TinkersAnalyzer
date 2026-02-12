package pelemenguin.tinkersanalyzer.library;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class Analyzer {

    private HashMap<UUID, Pair<ResourceLocation, CompoundTag>> graphs = new HashMap<>();

    public Map<UUID, Pair<ResourceLocation, CompoundTag>> getAllGraphs() {
        return Map.copyOf(graphs);
    }

    public CompoundTag createOrGetGraphData(UUID uuid, ResourceLocation graphId) {
        Pair<ResourceLocation, CompoundTag> pair = graphs.get(uuid);
        CompoundTag data;
        if (pair == null) {
            data = new CompoundTag();
            pair = Pair.of(graphId, data);
            graphs.put(uuid, pair);
        } else {
            data = pair.getSecond();
        }
        return data;
    }

    public boolean isEmpty() {
        return this.graphs.isEmpty();
    }

}
