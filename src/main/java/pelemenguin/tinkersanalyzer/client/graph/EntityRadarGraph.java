package pelemenguin.tinkersanalyzer.client.graph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import pelemenguin.tinkersanalyzer.client.graph.element.RadarGraphElement;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerModifiers;

public class EntityRadarGraph extends AnalyzerGraph {

    protected RadarGraphElement element;

    public EntityRadarGraph(CompoundTag tag) {
        super(tag);

        this.elements.clear();
        this.addElement(this.element);
    }

    @Override
    public void load(CompoundTag tag) {
        this.color = TinkersAnalyzerModifiers.ENTITY_RADAR.get().getColor();
        if (this.element == null) {
            this.element = new RadarGraphElement(15).color(this.color);
        }
        ResourceLocation requiredType = ResourceLocation.tryParse(tag.getString("entityType"));
        if (requiredType == null) return;
        EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(requiredType);
        if (type == null) return;
        this.element.trackEntity(type);
    }

}
