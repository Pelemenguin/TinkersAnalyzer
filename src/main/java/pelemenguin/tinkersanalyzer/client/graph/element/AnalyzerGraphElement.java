package pelemenguin.tinkersanalyzer.client.graph.element;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;

public abstract class AnalyzerGraphElement {

    public int x;
    public int y;

    public abstract void draw(GuiGraphics guiGraphics);

    public void render(GuiGraphics guiGraphics) {
        PoseStack pose = guiGraphics.pose();

        pose.translate(this.x, this.y, 0);
        this.draw(guiGraphics);
        pose.translate(-this.x, -this.y, 0);
    }

    public abstract int getWidth();
    public abstract int getHeight();

}
