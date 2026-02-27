package pelemenguin.tinkersanalyzer.content.item;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerGraphs;
import pelemenguin.tinkersanalyzer.library.Analyzer;
import pelemenguin.tinkersanalyzer.library.text.TextTransformer;
import pelemenguin.tinkersanalyzer.library.text.TransformableComponent;

public class EntityRadarItem extends Item implements IAnalyzerItem {

    public static final UUID GRAPH_UUID = UUID.fromString("d009369d-61ed-4228-b5b7-a32d118945a4");

    public EntityRadarItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void addGraph(ItemStack stack, EquipmentSlot slot, Analyzer analyzer) {
        CompoundTag data = analyzer.createOrGetGraphData(GRAPH_UUID, TinkersAnalyzerGraphs.ENTITY_RADAR);
        String entityType = getTrackingEntityName(stack);
        if (entityType != null) {
            TinkersAnalyzerGraphs.entityRadarGraphData(data, entityType);
        }
    }

    @Nullable
    public static String getTrackingEntityName(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (nbt != null) {
            String trackingEntity = nbt.getString("TrackingEntity");
            return trackingEntity.isEmpty() ? null : trackingEntity;
        }
        return null;
    }

    public static final TransformableComponent[] TOOLTIPS = new TransformableComponent[] {
        new TransformableComponent(TinkersAnalyzer.makeTranslation("item", "entity_radar.tooltip.1"), TextTransformer.tooltipsInstance),
        new TransformableComponent(TinkersAnalyzer.makeTranslation("item", "entity_radar.tooltip.2"), TextTransformer.tooltipsInstance),
        new TransformableComponent(TinkersAnalyzer.makeTranslation("item", "entity_radar.tooltip.3"), TextTransformer.tooltipsInstance),
        new TransformableComponent(TinkersAnalyzer.makeTranslation("item", "entity_radar.tooltip.4"), TextTransformer.tooltipsInstance)
    };
    public static final TransformableComponent TOOLTIP_TRACKING = new TransformableComponent(
            TinkersAnalyzer.makeTranslation("item", "entity_radar.tooltip.tracking"),
            TextTransformer.tooltipsInstance
        );
    private static final Component TOOLTIP_TRACKING_UNKNOWN = 
            TinkersAnalyzer.makeTranslation("item", "entity_radar.tooltip.tracking.unknown")
                .withStyle(TextTransformer.TOOLTIP_BOLD_STYLE);
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> component, TooltipFlag tooltipFlag) {
        String entityType = getTrackingEntityName(stack);
        if (entityType == null) {
            for (TransformableComponent tooltip : TOOLTIPS) {
                component.add(tooltip.get());
            }
        } else {
            EntityType<?> type = EntityType.byString(entityType).orElse(null);
            Component name;
            if (type != null) {
                name = type.getDescription().copy().withStyle(TextTransformer.TOOLTIP_BOLD_STYLE);
            } else {
                name = TOOLTIP_TRACKING_UNKNOWN;
            }
            component.add(TOOLTIP_TRACKING.get().copy().append(name));
        }
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity,
            InteractionHand hand) {
        stack.getOrCreateTag().putString("TrackingEntity", EntityType.getKey(entity.getType()).toString());
        return InteractionResult.CONSUME;
    }

}
