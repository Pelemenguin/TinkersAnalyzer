package pelemenguin.tinkersanalyzer.content.modifier;

import java.util.UUID;

import net.minecraft.network.chat.Component;
import pelemenguin.tinkersanalyzer.library.Analyzer;
import pelemenguin.tinkersanalyzer.library.TinkersAnalyzerGraphs;
import pelemenguin.tinkersanalyzer.library.hook.DisplayAnalyzerGraphModifierHook;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class TinkersAnalyzerModifier extends Modifier implements DisplayAnalyzerGraphModifierHook {

    @Override
    protected void registerHooks(Builder hookBuilder) {
        hookBuilder.addHook(this, DisplayAnalyzerGraphModifierHook.INSTANCE);
    }

    private static final UUID graphUuid = UUID.randomUUID();

    @Override
    public void addGraph(IToolStackView tool, ModifierEntry modifier, Analyzer analyzer) {
        TinkersAnalyzerGraphs.plainTextGraphData(
                analyzer.createOrGetGraphData(graphUuid, TinkersAnalyzerGraphs.PLAIN_TEXT),
                Component.literal("TEST"),
                96,
                0x00FF00
            );
    }

}
