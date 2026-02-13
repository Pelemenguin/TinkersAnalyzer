package pelemenguin.tinkersanalyzer.client.graph.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import pelemenguin.tinkersanalyzer.client.graph.AnalyzerGraph;

public class TitleGraphElement extends AnalyzerGraphElement {

    AnalyzerGraph parent;
    Component text;
    int minWidth;

    public TitleGraphElement(Component text, AnalyzerGraph parent, int minWidth) {
        this.text = text;
        this.parent = parent;
        this.minWidth = minWidth;
    }

    @Override
    public void draw(GuiGraphics guiGraphics) {
        int color = this.parent.getColor();

        guiGraphics.drawString(Minecraft.getInstance().font, this.text, 0, 0, color, false);
        guiGraphics.hLine(- this.x - 2, this.parent.getWidth() - this.x - 3, this.getHeight() - 2, (0xFF000000) | color);
    }

    @Override
    public int getWidth() {
        return minWidth;
    }

    @Override
    public int getHeight() {
        return (int) (2 + Minecraft.getInstance().font.lineHeight);
    }

}
