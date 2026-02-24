package pelemenguin.tinkersanalyzer.client.graph;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import pelemenguin.tinkersanalyzer.client.graph.element.FluidTankBarGraphElement;
import pelemenguin.tinkersanalyzer.client.graph.element.ItemGraphElement;
import pelemenguin.tinkersanalyzer.client.graph.element.ProgressBarGraphElement;
import pelemenguin.tinkersanalyzer.client.graph.element.TaggedTextGraphElement;
import pelemenguin.tinkersanalyzer.client.util.NumberFormatter;
import pelemenguin.tinkersanalyzer.content.modifier.FluidGaugeModifier.GaugeType;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class FluidGaugeGraph extends AnalyzerGraph {

    private static final Logger LOGGER = LogUtils.getLogger();

    public FluidGaugeGraph(CompoundTag tag) {
        super(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.elements.clear();
        CompoundTag slotList = tag.getCompound("slots");

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        int x = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ToolStack tool;
            ItemStack item;
            GaugeType type;

            try {
                type = GaugeType.valueOf(slotList.getString(slot.toString()));
            } catch (Throwable t) {
                continue;
            }

            item = player.getItemBySlot(slot);
            if (!(item.getItem() instanceof IModifiable)) {
                LOGGER.error("Not an IModifiable: {}", item.getItem());
                continue;
            }
            tool = ToolStack.from(item);

            int capacity = ToolTankHelper.TANK_HELPER.getCapacity(tool);
            FluidStack fluid = ToolTankHelper.TANK_HELPER.getFluid(tool);

            var progress = ProgressBarGraphElement.newProgressBar().maxValue(capacity).currentValue(fluid.getAmount());
            ProgressBarGraphElement bar;
            if (type == GaugeType.COPPER) {
                bar = new ProgressBarGraphElement(this, 64, true, fluid.getFluid().getFluidType().isLighterThanAir());
                bar.x = x;
                bar.y = 9;
                bar.progressBar(progress);
                this.addElement(bar);
            } else {
                bar = new FluidTankBarGraphElement(this, 64, true).fluid(fluid);
                bar.x = x;
                bar.y = 9;
                bar.progressBar(progress);
                this.addElement(bar);
            }

            TaggedTextGraphElement text = new TaggedTextGraphElement(() -> Component.literal(NumberFormatter.integer(100000).formatNumber(fluid.getAmount())));
            text.x = x + 1;
            text.y = bar.y + bar.getHeight() + 1;
            text.colored(0xFF000000 | this.color);
            this.addElement(text);

            ItemGraphElement itemElement = new ItemGraphElement(item);
            itemElement.x = x + 1;
            itemElement.scale = 0.5f;
            this.addElement(itemElement);

            x += bar.getWidth() + 1;
        }
    }

}
