package pelemenguin.tinkersanalyzer.content.item;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
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

    public static AnalyzerItem createItem(IAnalyzerItem addGraphLambda, Item.Properties properties, Component ...components) {
        return new AnalyzerItem(properties) {
            @Override
            public void addGraph(ItemStack stack, EquipmentSlot slot, Analyzer analyzer) {
                addGraphLambda.addGraph(stack, slot, analyzer);
            }
            @Override
            public void appendHoverText(ItemStack p_41421_, Level p_41422_, List<Component> p_41423_,
                    TooltipFlag p_41424_) {
                for (Component component : components) {
                    p_41423_.add(component);
                }
            }
        };
    }

    public static abstract class AnalyzerItem extends Item implements IAnalyzerItem {

        public AnalyzerItem(Properties p_41383_) {
            super(p_41383_);
        }

    }

}
