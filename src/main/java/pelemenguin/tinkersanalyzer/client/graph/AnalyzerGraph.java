package pelemenguin.tinkersanalyzer.client.graph;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import pelemenguin.tinkersanalyzer.client.graph.element.AnalyzerGraphElement;
import pelemenguin.tinkersanalyzer.client.util.render.QuadHelper;

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
    private static final int BACKGROUND_ALPHA_SELECTED = 127;

    private float selectProgress = 0.0f;
    private void drawBackground(GuiGraphics guiGraphics) {
        int w = this.getWidth();
        int h = this.getHeight();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, -0.0625f);
        Matrix4f matrix = guiGraphics.pose().last().pose();
        int opaque = 0xFF000000 | color;
        int backgroundAlpha = (int) (BACKGROUND_ALPGA * (1 - this.selectProgress) + BACKGROUND_ALPHA_SELECTED * (this.selectProgress));

        QuadHelper.prepareDrawQuads();
        QuadHelper.drawAxisAlignedQuad(0, 0, w, 1, matrix, opaque);
        QuadHelper.drawAxisAlignedQuad(0, h-1, w, h, matrix, opaque);
        QuadHelper.drawAxisAlignedQuad(0, 0, 1, h, matrix, opaque);
        QuadHelper.drawAxisAlignedQuad(w-1, 0, w, h, matrix, opaque);
        QuadHelper.drawAxisAlignedQuad(1, 1, w, h, matrix, (backgroundAlpha << 24) | color);

        if (selectProgress > 0.0f) {
            float outerMinX = - selectProgress * 2.0f;
            float outerMinY = - selectProgress * 2.0f;
            float outerMaxX = w + selectProgress * 2.0f;
            float outerMaxY = h + selectProgress * 2.0f;
            // Top Left
            QuadHelper.drawAxisAlignedQuad(outerMinX, outerMinY, outerMinX + 5, outerMinY + 1, matrix, opaque);
            QuadHelper.drawAxisAlignedQuad(outerMinX, outerMinY, outerMinX + 1, outerMinY + 5, matrix, opaque);
            // Bottom Left
            QuadHelper.drawAxisAlignedQuad(outerMinX, outerMaxY - 5, outerMinX + 1, outerMaxY, matrix, opaque);
            QuadHelper.drawAxisAlignedQuad(outerMinX, outerMaxY - 1, outerMinX + 5, outerMaxY, matrix, opaque);
            // Bottom Right
            QuadHelper.drawAxisAlignedQuad(outerMaxX - 5, outerMaxY - 1, outerMaxX, outerMaxY, matrix, opaque);
            QuadHelper.drawAxisAlignedQuad(outerMaxX - 1, outerMaxY - 5, outerMaxX, outerMaxY, matrix, opaque);
            // Top Right
            QuadHelper.drawAxisAlignedQuad(outerMaxX - 5, outerMinY, outerMaxX, outerMinY + 1, matrix, opaque);
            QuadHelper.drawAxisAlignedQuad(outerMaxX - 1, outerMinY, outerMaxX, outerMinY + 5, matrix, opaque);
        }

        QuadHelper.finishDrawQuads();
        guiGraphics.pose().popPose();
    }

    public void render(GuiGraphics guiGraphics, boolean isSelected) {
        PoseStack pose = guiGraphics.pose();

        drawBackground(guiGraphics);
        if (isSelected) {
            this.selectProgress += 0.2f * Minecraft.getInstance().getDeltaFrameTime();
            if (this.selectProgress >= 1.0f) this.selectProgress = 1.0f;
        } else {
            this.selectProgress -= 0.2f * Minecraft.getInstance().getDeltaFrameTime();
            if (this.selectProgress <= 0.0f) this.selectProgress = 0.0f;
        }

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
