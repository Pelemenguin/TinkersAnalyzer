package pelemenguin.tinkersanalyzer.content.modifier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerGraphs;
import pelemenguin.tinkersanalyzer.content.item.EntityRadarItem;
import pelemenguin.tinkersanalyzer.library.Analyzer;
import pelemenguin.tinkersanalyzer.library.hook.DisplayAnalyzerGraphModifierHook;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class EntityRadarModifier extends NoLevelsModifier implements DisplayAnalyzerGraphModifierHook, EntityInteractionModifierHook {

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, DisplayAnalyzerGraphModifierHook.INSTANCE);
        hookBuilder.addHook(this, ModifierHooks.ENTITY_INTERACT);
    }

    @Override
    public void addGraph(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, Analyzer analyzer) {
        CompoundTag tag = analyzer.createOrGetGraphData(
                EntityRadarItem.GRAPH_UUID,
                TinkersAnalyzerGraphs.ENTITY_RADAR
                // TODO: Add a default layout
            );
        String entityType = tool.getPersistentData().getString(modifier.getId());
        if (entityType.isEmpty()) return;
        TinkersAnalyzerGraphs.entityRadarGraphData(tag, entityType);
    }

    @Override
    public InteractionResult afterEntityUse(IToolStackView tool, ModifierEntry modifier, Player player,
            LivingEntity target, InteractionHand hand, InteractionSource source) {
        tool.getPersistentData().putString(modifier.getId(), EntityType.getKey(target.getType()).toString());
        return InteractionResult.CONSUME;
    }

}
