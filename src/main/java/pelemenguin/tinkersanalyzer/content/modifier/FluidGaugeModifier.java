package pelemenguin.tinkersanalyzer.content.modifier;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerGraphs;
import pelemenguin.tinkersanalyzer.library.Analyzer;
import pelemenguin.tinkersanalyzer.library.AnalyzerLayoutEntry;
import pelemenguin.tinkersanalyzer.library.hook.DisplayAnalyzerGraphModifierHook;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class FluidGaugeModifier extends NoLevelsModifier implements DisplayAnalyzerGraphModifierHook {

    public static final UUID GRAPH_UUID = UUID.fromString("817c57c0-b0f0-4953-aa1b-dd39e037be34");
    public static enum GaugeType {
        COPPER, OBSIDIAN
    }

    GaugeType type;

    public FluidGaugeModifier(GaugeType type) {
        this.type = type;
    }

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, DisplayAnalyzerGraphModifierHook.INSTANCE);
    }

    @Override
    public void addGraph(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, Analyzer analyzer) {
        CompoundTag arg = analyzer.createOrGetGraphData(GRAPH_UUID, TinkersAnalyzerGraphs.FLUID_GAUGE, new AnalyzerLayoutEntry(-45, 5, 220));
        if (ToolTankHelper.TANK_HELPER.getCapacity(tool) > 0) {
            TinkersAnalyzerGraphs.fluidGaugeGraphData(arg, slot, type);
        }
    }

}
