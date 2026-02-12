package pelemenguin.tinkersanalyzer.library.hook;

import java.util.Collection;

import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.library.Analyzer;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public interface DisplayAnalyzerGraphModifierHook {

    void addGraph(IToolStackView tool, ModifierEntry modifier, Analyzer analyzer);

    record AllMerger(Collection<DisplayAnalyzerGraphModifierHook> modules) implements DisplayAnalyzerGraphModifierHook {
        @Override
        public void addGraph(IToolStackView tool, ModifierEntry modifier, Analyzer analyzer) {
             for (DisplayAnalyzerGraphModifierHook module : this.modules) {
                 module.addGraph(tool, modifier, analyzer);
             }
        }
    }

    public static final ModuleHook<DisplayAnalyzerGraphModifierHook> INSTANCE = ModifierHooks.register(
            TinkersAnalyzer.makeResource("display_analyzer_graph"),
            DisplayAnalyzerGraphModifierHook.class,
            AllMerger::new,
            (tool, entry, analyzer) -> {}
        );

}
