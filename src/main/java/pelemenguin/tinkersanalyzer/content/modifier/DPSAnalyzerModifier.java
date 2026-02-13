package pelemenguin.tinkersanalyzer.content.modifier;

import java.util.UUID;

import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerGraphs;
import pelemenguin.tinkersanalyzer.library.Analyzer;
import pelemenguin.tinkersanalyzer.library.hook.DisplayAnalyzerGraphModifierHook;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class DPSAnalyzerModifier extends Modifier implements DisplayAnalyzerGraphModifierHook {

    public static final UUID GRAPH_UUID = UUID.fromString("291b4f42-6559-4bda-be8f-508329b7cffc");

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, DisplayAnalyzerGraphModifierHook.INSTANCE);
    }

    @Override
    public void addGraph(IToolStackView tool, ModifierEntry modifier, Analyzer analyzer) {
        analyzer.createOrGetGraphData(GRAPH_UUID, TinkersAnalyzerGraphs.DPS);
    }

}
