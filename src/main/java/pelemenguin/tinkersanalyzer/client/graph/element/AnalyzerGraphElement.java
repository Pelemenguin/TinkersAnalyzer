package pelemenguin.tinkersanalyzer.client.graph.element;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Abstract base class of graph elements.
 */
public abstract class AnalyzerGraphElement {

    public int x;
    public int y;

    /**
     * Draw the content of the GraphElement.
     * When drawing, simply use (0, 0) as the top-left corner,
     * as the PoseStack has already been translated to the position of the element in {@link AnalyzerGraphElement#render}.
     * @param guiGraphics The GuiGraphics
     */
    public abstract void draw(GuiGraphics guiGraphics);

    /**
     * Render the GraphElement.
     * @param guiGraphics The GuiGraphics
     */
    public void render(GuiGraphics guiGraphics) {
        PoseStack pose = guiGraphics.pose();

        pose.translate(this.x, this.y, 0);
        this.draw(guiGraphics);
        pose.translate(-this.x, -this.y, 0);
    }

    /**
     * Get the width of the GraphElement.
     * @return An integer as width
     */
    public abstract int getWidth();
    /**
     * Get the height of the GraphElement.
     * @return An integer as height
     */
    public abstract int getHeight();

}
