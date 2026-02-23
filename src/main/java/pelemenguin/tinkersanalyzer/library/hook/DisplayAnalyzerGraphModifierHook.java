package pelemenguin.tinkersanalyzer.library.hook;

import java.util.Collection;

import net.minecraft.world.entity.EquipmentSlot;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.library.Analyzer;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Hook for adding analyzer graphs to the {@link pelemenguin.tinkersanalyzer.client.AnalyzerOverlay AnalyzerOverlay} when rendering.
 */
public interface DisplayAnalyzerGraphModifierHook {

    /**
     * Add new analyzer graphs to the {@link pelemenguin.tinkersanalyzer.client.AnalyzerOverlay AnalyzerOverlay}.
     * 
     * @param tool     Tool instance
     * @param modifier The modifier and level
     * @param slot     The {@link EquipmentSlot}
     * @param analyzer An {@link pelemenguin.tinkersanalyzer.library.Analyzer Analyzer} instance for collecting analyzer graphs
     */
    void addGraph(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, Analyzer analyzer);

    record AllMerger(Collection<DisplayAnalyzerGraphModifierHook> modules) implements DisplayAnalyzerGraphModifierHook {
        @Override
        public void addGraph(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, Analyzer analyzer) {
             for (DisplayAnalyzerGraphModifierHook module : this.modules) {
                 module.addGraph(tool, modifier, slot, analyzer);
             }
        }
    }

    /**
     * The {@link slimeknights.tconstruct.library.module.ModuleHook ModuleHook} instance for being added when registering modifiers.
     */
    public static final ModuleHook<DisplayAnalyzerGraphModifierHook> INSTANCE = ModifierHooks.register(
            TinkersAnalyzer.makeResource("display_analyzer_graph"),
            DisplayAnalyzerGraphModifierHook.class,
            AllMerger::new,
            (tool, entry, slot, analyzer) -> {}
        );

}
