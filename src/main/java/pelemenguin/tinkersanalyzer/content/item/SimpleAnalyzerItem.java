package pelemenguin.tinkersanalyzer.content.item;

import java.util.List;
import java.util.UUID;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import pelemenguin.tinkersanalyzer.library.Analyzer;
import pelemenguin.tinkersanalyzer.library.AnalyzerLayoutEntry;
import pelemenguin.tinkersanalyzer.library.text.TextTransformer;
import pelemenguin.tinkersanalyzer.library.text.TransformableComponent;

public class SimpleAnalyzerItem extends Item implements IAnalyzerItem {

    public UUID graphUuid;
    public ResourceLocation graphId;
    private AnalyzerLayoutEntry defaultLayout;
    private TransformableComponent[] components;

    public SimpleAnalyzerItem(Properties properties, UUID graphUuid, ResourceLocation graphId, AnalyzerLayoutEntry defaultLayout, Component ...components) {
        super(properties);
        this.graphUuid = graphUuid;
        this.graphId = graphId;
        this.defaultLayout = defaultLayout;
        this.components = new TransformableComponent[components.length];
        for (int i = 0; i < components.length; i++) {
            this.components[i] = new TransformableComponent(components[i], TextTransformer.tooltipsInstance);
        }
    }

    public void addGraph(Analyzer analyzer) {
        analyzer.createOrGetGraphData(this.graphUuid, this.graphId, this.defaultLayout);
    }

    @Override
    public void addGraph(ItemStack stack, EquipmentSlot slot, Analyzer analyzer) {
        analyzer.createOrGetGraphData(this.graphUuid, this.graphId, this.defaultLayout);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        for (TransformableComponent component : this.components) {
            p_41423_.add(component.get());
        }
    }

}
