package pelemenguin.tinkersanalyzer.client.graph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.client.graph.element.RadarGraphElement;
import pelemenguin.tinkersanalyzer.client.graph.element.TaggedTextGraphElement;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerModifiers;

public class EntityRadarGraph extends AnalyzerGraph {

    protected RadarGraphElement element;
    protected EntityType<?> tracking;

    public EntityRadarGraph(CompoundTag tag) {
        super(tag);

        this.elements.clear();
        this.addElement(this.element);
        if (this.tracking != null) {
            TaggedTextGraphElement text = new TaggedTextGraphElement(this.tracking::getDescription).colored(this.color).tagAbove(
                    TinkersAnalyzer.makeModifierTranslation("entity_radar.tracking"));
            text.x = this.element.getWidth() + 1;
            text.scale = 2;
            this.addElement(text);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        this.color = TinkersAnalyzerModifiers.ENTITY_RADAR.get().getColor();
        if (this.element == null) {
            this.element = new RadarGraphElement(15).color(this.color);
        }
        String entityName = tag.getString("entityType");
        if (entityName.isEmpty()) return;
        ResourceLocation requiredType = ResourceLocation.tryParse(entityName);
        if (requiredType == null) return;
        EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(requiredType);
        if (type == null) return;
        this.element.trackEntity(type);
        this.tracking = type;
    }

}
