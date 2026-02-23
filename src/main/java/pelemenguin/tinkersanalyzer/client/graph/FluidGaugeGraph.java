package pelemenguin.tinkersanalyzer.client.graph;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import pelemenguin.tinkersanalyzer.client.graph.element.FluidTankBarGraphElement;
import pelemenguin.tinkersanalyzer.client.graph.element.ProgressBarGraphElement;
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
        // TODO: This loop cannot ensure order
        //       Change to let EquipmentSlots find their own tank later
        for (String es : slotList.getAllKeys()) {
            ToolStack tool;
            ItemStack item;
            GaugeType type;
            EquipmentSlot slot;

            try {
                type = GaugeType.valueOf(slotList.getString(es));
            } catch (Throwable t) {
                LOGGER.error("Invalid gauge type: {}", slotList.getString(es));
                return;
            }
            try {
                slot = EquipmentSlot.valueOf(es);
            } catch (Throwable t) {
                LOGGER.error("Invalid equipment slot: {}", es);
                return;
            }

            item = player.getItemBySlot(slot);
            if (!(item.getItem() instanceof IModifiable)) {
                LOGGER.error("Not an IModifiable: {}", item.getItem());
            }
            tool = ToolStack.from(item);

            int capacity = ToolTankHelper.TANK_HELPER.getCapacity(tool);
            FluidStack fluid = ToolTankHelper.TANK_HELPER.getFluid(tool);
            LogUtils.getLogger().debug("capacity: {}, fluid: {}", capacity, fluid);

            var progress = ProgressBarGraphElement.newProgressBar().maxValue(capacity).currentValue(fluid.getAmount());
            if (type == GaugeType.COPPER) {
                ProgressBarGraphElement bar = new ProgressBarGraphElement(this, 64, true, fluid.getFluid().getFluidType().isLighterThanAir());
                bar.x = x;
                x += bar.getWidth() + 1;
                bar.progressBar(progress);
                this.addElement(bar);
            } else {
                FluidTankBarGraphElement bar = new FluidTankBarGraphElement(this, 64, true).fluid(fluid);
                bar.x = x;
                x += bar.getWidth() + 1;
                bar.progressBar(progress);
                this.addElement(bar);
            }
        }
    }

}
