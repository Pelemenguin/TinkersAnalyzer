package pelemenguin.tinkersanalyzer.client.graph;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import pelemenguin.tinkersanalyzer.client.graph.element.AnalyzerGraphElement;

public class AnalyzerGraph {

    List<AnalyzerGraphElement> elements = new ArrayList<>();

    int color;

    int width = -1;
    int height = -1;

    public AnalyzerGraph(CompoundTag tag) {
        this.load(tag);
    }

    public void load(CompoundTag tag) {
        this.color = tag.getInt("color");
    }

    private static final int BACKGROUND_ALPGA = 31;
    private void drawBackground(GuiGraphics guiGraphics) {
        int w = this.getWidth();
        int h = this.getHeight();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, -0.0625f);
        guiGraphics.fill(0, 0, w, 1, (0xFF000000) | color);
        guiGraphics.fill(0, h-1, w, h, (0xFF000000) | color);
        guiGraphics.fill(0, 0, 1, h, (0xFF000000) | color);
        guiGraphics.fill(w-1, 0, w, h, (0xFF000000) | color);
        guiGraphics.fill(1, 1, w, h, (BACKGROUND_ALPGA << 24) | color);
        guiGraphics.pose().popPose();
    }

    public void render(GuiGraphics guiGraphics) {
        PoseStack pose = guiGraphics.pose();

        drawBackground(guiGraphics);

        pose.translate(2, 2, 0);
        for (AnalyzerGraphElement element : elements) {
            element.render(guiGraphics);
        }
        pose.translate(-2, -2, 0);
    }

    public void addElement(AnalyzerGraphElement element) {
        this.elements.add(element);
    }

    public int getWidth() {
        if (this.width >= 0) {
            return this.width;
        }

        int maxX = 0;

        for (AnalyzerGraphElement element : this.elements) {
            int thisX = (int) (element.x + element.getWidth() * element.scale);
            if (thisX > maxX) {
                maxX = thisX;
            }
        }

        this.width = maxX + 4;
        return this.width;
    }

    public int getHeight() {
        if (this.height >= 0) {
            return this.height;
        }

        int maxY = 0;

        for (AnalyzerGraphElement element : this.elements) {
            int thisY = (int) (element.y + element.getHeight() * element.scale);
            if (thisY > maxY) {
                maxY = thisY;
            }
        }

        this.height = maxY + 4;
        return this.height;
    }

    public void refreshSize() {
        this.width = -1;
        this.height = -1;
    }

    public int getColor() {
        return this.color;
    }

}
