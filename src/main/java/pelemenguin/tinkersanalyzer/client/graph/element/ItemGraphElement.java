package pelemenguin.tinkersanalyzer.client.graph.element;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class ItemGraphElement extends AnalyzerGraphElement {

    ItemStack item;

    public ItemGraphElement(ItemStack item) {
        this.item = item;
    }

    @Override
    public void draw(GuiGraphics guiGraphics) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, -150);
        guiGraphics.renderItem(item, 0, 0);
        guiGraphics.pose().popPose();
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

}
