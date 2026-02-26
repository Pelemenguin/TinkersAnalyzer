package pelemenguin.tinkersanalyzer.content.modifier;

import net.minecraft.world.entity.EquipmentSlot;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerItems;
import pelemenguin.tinkersanalyzer.library.Analyzer;
import pelemenguin.tinkersanalyzer.library.hook.DisplayAnalyzerGraphModifierHook;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class EntityRadarModifier extends Modifier implements DisplayAnalyzerGraphModifierHook {

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, DisplayAnalyzerGraphModifierHook.INSTANCE);
    }

    @Override
    public void addGraph(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, Analyzer analyzer) {
        TinkersAnalyzerItems.ENTITY_RADAR.get().addGraph(analyzer);
    }

}
