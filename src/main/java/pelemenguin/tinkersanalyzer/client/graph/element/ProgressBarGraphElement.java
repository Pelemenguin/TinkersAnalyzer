package pelemenguin.tinkersanalyzer.client.graph.element;

import javax.annotation.Nullable;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import pelemenguin.tinkersanalyzer.client.graph.AnalyzerGraph;
import pelemenguin.tinkersanalyzer.client.util.render.QuadHelper;

public class ProgressBarGraphElement extends AnalyzerGraphElement {

    public static final int BAR_WIDTH = 8;
    private static final int CONTENT_ALPHA = 0x7F000000;

    int color;
    int barLength;
    boolean vertical;
    boolean reversed;

    @Nullable
    protected ProgressBar progressBar;

    public ProgressBarGraphElement(AnalyzerGraph parent, int barLength, boolean vertical, boolean reversed) {
        this.color = parent.getColor();
        this.barLength = barLength;
        this.vertical = vertical;
        this.reversed = reversed;
    }

    public ProgressBarGraphElement(AnalyzerGraph parent, int barLength, boolean vertical) {
        this(parent, barLength, vertical, false);
    }

    public ProgressBarGraphElement(AnalyzerGraph parent, int barLength) {
        this(parent, barLength, false, false);
    }

    @Override
    public void draw(GuiGraphics guiGraphics) {
        ProgressBar progressBar = this.progressBar;
        if (progressBar == null) return;

        PoseStack pose = guiGraphics.pose();
        Matrix4f matrix = pose.last().pose();

        QuadHelper.prepareDrawQuads();

        if (this.vertical) {
            QuadHelper.drawAxisAlignedBorderedQuad(0, 0, BAR_WIDTH + 2, this.barLength + 2, 1, matrix, 0, 0xFF000000 | this.color);
            if (this.reversed) {
                QuadHelper.drawAxisAlignedQuad(1, 1, BAR_WIDTH + 1, progressBar.getProgress(this) + 1, matrix, CONTENT_ALPHA | this.color);
            } else {
                QuadHelper.drawAxisAlignedQuad(1, this.barLength - progressBar.getProgress(this) + 1, BAR_WIDTH + 1, this.barLength + 1, matrix, CONTENT_ALPHA | this.color);
            }
        } else {
            QuadHelper.drawAxisAlignedBorderedQuad(0, 0, this.barLength + 2, BAR_WIDTH + 2, 1, matrix, 0, 0xFF000000 | this.color);
            if (this.reversed) {
                QuadHelper.drawAxisAlignedQuad(this.barLength - progressBar.getProgress(this) + 1, 1, this.barLength + 1, BAR_WIDTH + 1, matrix, CONTENT_ALPHA | this.color);
            } else {
                QuadHelper.drawAxisAlignedQuad(1, 1, progressBar.getProgress(this) + 1, BAR_WIDTH + 1, matrix, CONTENT_ALPHA | this.color);
            }
        }

        QuadHelper.finishDrawQuads();
    }

    public void progressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public static ProgressBar newProgressBar() {
        return new ProgressBar();
    }

    @Override
    public int getWidth() {
        return this.vertical ? BAR_WIDTH + 2 : this.barLength + 2;
    }

    @Override
    public int getHeight() {
        return this.vertical ? this.barLength + 2 : BAR_WIDTH + 2;
    }

    public static class ProgressBar {

        public float minValue = 0;
        public float maxValue = 100;
        public float currentValue = 0;

        public ProgressBar minValue(float minValue) {
            this.minValue = minValue;
            return this;
        }

        public ProgressBar maxValue(float maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public ProgressBar currentValue(float currentValue) {
            this.currentValue = currentValue;
            return this;
        }

        protected float getProgress(ProgressBarGraphElement parent) {
            return (this.currentValue - this.minValue) / (this.maxValue - this.minValue) * parent.barLength;
        }

    }

}
