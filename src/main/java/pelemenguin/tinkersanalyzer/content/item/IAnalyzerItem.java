package pelemenguin.tinkersanalyzer.content.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import pelemenguin.tinkersanalyzer.library.Analyzer;

@FunctionalInterface
public interface IAnalyzerItem {

    public void addGraph(ItemStack stack, EquipmentSlot slot, Analyzer analyzer);

    public static AnalyzerItem createItem(IAnalyzerItem addGraphLambda, Item.Properties properties) {
        return new AnalyzerItem(properties) {
            @Override
            public void addGraph(ItemStack stack, EquipmentSlot slot, Analyzer analyzer) {
                addGraphLambda.addGraph(stack, slot, analyzer);
            }
        };
    }

    public static AnalyzerItem createItem(IAnalyzerItem addGraphLambda) {
        return createItem(addGraphLambda);
    }

    public static abstract class AnalyzerItem extends Item implements IAnalyzerItem {

        public AnalyzerItem(Properties p_41383_) {
            super(p_41383_);
        }

    }

}
