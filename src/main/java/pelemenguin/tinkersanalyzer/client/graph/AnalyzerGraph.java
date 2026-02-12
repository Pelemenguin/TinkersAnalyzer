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

    private static final int BACKGROUND_ALPGA = 63;
    private void drawBackground(GuiGraphics guiGraphics) {
        int width = this.getWidth();
        int height = this.getHeight();

        guiGraphics.hLine(0, width - 1, 0, (0xFF000000) | color);
        guiGraphics.hLine(0, width - 1, height - 1, (0xFF000000) | color);
        guiGraphics.vLine(0, 0, height - 1, (0xFF000000) | color);
        guiGraphics.vLine(width - 1, 0, height - 1, (0xFF000000) | color);
        guiGraphics.fill(0, 0, width - 1, height - 1, (BACKGROUND_ALPGA << 24) | color);
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
            int thisX = element.x + element.getWidth();
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
            int thisY = element.x + element.getHeight();
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

}
