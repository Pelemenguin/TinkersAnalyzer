package pelemenguin.tinkersanalyzer.content.item;

import java.util.List;
import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerGraphs;
import pelemenguin.tinkersanalyzer.library.Analyzer;
import pelemenguin.tinkersanalyzer.library.AnalyzerLayoutEntry;

public class DPSAnalyzerItem extends Item implements IAnalyzerItem {

    public static final UUID GRAPH_UUID = UUID.fromString("291b4f42-6559-4bda-be8f-508329b7cffc");

    public DPSAnalyzerItem(Properties p_41383_) {
        super(p_41383_);
    }

    public static void addGraph(Analyzer analyzer) {
        analyzer.createOrGetGraphData(GRAPH_UUID, TinkersAnalyzerGraphs.DPS, new AnalyzerLayoutEntry(30.0f, -10.0f, 192.0f));
    }

    @Override
    public void addGraph(ItemStack stack, EquipmentSlot slot, Analyzer analyzer) {
        addGraph(analyzer);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add(TinkersAnalyzer.makeTranslation("item", "dps_analyzer.tooltip").withStyle(ChatFormatting.GRAY));
    }

}
