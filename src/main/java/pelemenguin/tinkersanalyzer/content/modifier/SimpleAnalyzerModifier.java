package pelemenguin.tinkersanalyzer.content.modifier;

import java.util.UUID;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import pelemenguin.tinkersanalyzer.library.Analyzer;
import pelemenguin.tinkersanalyzer.library.hook.DisplayAnalyzerGraphModifierHook;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class SimpleAnalyzerModifier extends NoLevelsModifier implements DisplayAnalyzerGraphModifierHook {

    protected UUID graphUuid;
    protected ResourceLocation graphId;

    public SimpleAnalyzerModifier(UUID graphUuid, ResourceLocation graphId) {
        super();
        this.graphUuid = graphUuid;
        this.graphId = graphId;
    }

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, DisplayAnalyzerGraphModifierHook.INSTANCE);
    }

    @Override
    public void addGraph(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, Analyzer analyzer) {
        analyzer.createOrGetGraphData(this.graphUuid, this.graphId);
    }

}
