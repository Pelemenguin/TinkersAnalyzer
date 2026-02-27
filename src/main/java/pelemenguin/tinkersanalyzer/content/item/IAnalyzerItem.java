package pelemenguin.tinkersanalyzer.content.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import pelemenguin.tinkersanalyzer.library.Analyzer;

/**
 * An interface for Analyzer Items.
 * Items that implemented this interface will be detected and added graphs to the {@link Analyzer} will be collected.
 */
@FunctionalInterface
public interface IAnalyzerItem {
    public void addGraph(ItemStack stack, EquipmentSlot slot, Analyzer analyzer);
}
