package pelemenguin.tinkersanalyzer.client.graph.element;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.LongSupplier;

import org.slf4j.Logger;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import pelemenguin.tinkersanalyzer.client.graph.AnalyzerGraph;

public class DiagramGraphElement extends AnalyzerGraphElement {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final AxisTickLabel DEFAULT_TICK_LABEL = (x) -> "%.2g".formatted(x);
    private static final float SMOOTH_FACTOR = 0.1f;
    private static final float VERTICAL_AXIS_BOUND_MULIPLIER = 1.2f;

    AnalyzerGraph parent;
    int width;
    int height;

    Component horizontalAxisName;
    Component verticalAxisName;
    float minX = 0;
    float maxX = 1;
    float minY = 0;
    float maxY = 1;
    float minMaxY = 1;
    float maxMinY = 0;
    int maxHorizontalTick;
    int maxVerticalTick;
    boolean fixMinY = true;
    boolean fixMaxY = true;
    boolean horizontalAxisIsTime;
    LongSupplier timeOrigin = () -> 0;
    AxisTickLabel horizontalTickLabel = DEFAULT_TICK_LABEL;
    AxisTickLabel verticalTickLabel = DEFAULT_TICK_LABEL;

    float targetMinY;
    float targetMaxY;

    public DiagramGraphElement(AnalyzerGraph parent, int width, int height) {
        this.width = width;
        this.height = height;
        this.parent = parent;

        this.maxHorizontalTick = this.width / 8;
        this.maxVerticalTick = this.height / 8;
    }

    ArrayDeque<Diagram> diagrams = new ArrayDeque<>();

    @Override
    public void draw(GuiGraphics guiGraphics) {
        guiGraphics.fill(0, 0, 1, this.height, 0xFF000000 | this.parent.getColor());
        guiGraphics.fill(0, this.height - 1, this.width, this.height, 0xFF000000 | this.parent.getColor());

        PoseStack pose = guiGraphics.pose();
        Font font = Minecraft.getInstance().font;
        int lineHeight = font.lineHeight;
        float horizontalAxisNameWidth = 0;
        if (this.horizontalAxisName != null) {
            horizontalAxisNameWidth = font.width(this.horizontalAxisName);
            pose.pushPose();
            pose.translate(this.width - 1, this.getHeight() - 2, 0);
            pose.scale(0.5f, 0.5f, 1.0f);
            guiGraphics.drawString(font, this.horizontalAxisName, -(int) horizontalAxisNameWidth, -lineHeight, this.parent.getColor(), false);
            pose.popPose();
            horizontalAxisNameWidth = (float) this.width - 0.5f * horizontalAxisNameWidth;
        }
        float verticalAxisNameHeight = 0;
        if (this.verticalAxisName != null) {
            pose.pushPose();
            pose.translate(2, 1, 0);
            pose.scale(0.5f, 0.5f, 1.0f);
            guiGraphics.drawString(font, this.verticalAxisName, 0, 0, this.parent.getColor(), false);
            pose.popPose();
            verticalAxisNameHeight = 0.5f * lineHeight;
        }

        drawReferenceLine(guiGraphics, horizontalAxisNameWidth, verticalAxisNameHeight);

        float minX = this.minX;
        float maxX = this.maxX;
        if (this.horizontalAxisIsTime) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                float curTime = (level.getGameTime() - this.timeOrigin.getAsLong()) + Minecraft.getInstance().getPartialTick();
                minX += curTime;
                maxX += curTime;
            }
        }

        float minYFromDiagrams = this.maxMinY;
        float maxYFromDiagrams = this.minMaxY;
        for (Diagram diagram : this.diagrams) {
            diagram.draw(guiGraphics, this, minX, maxX);
            float curMinY = diagram.minY * VERTICAL_AXIS_BOUND_MULIPLIER;
            float curMaxY = diagram.maxY * VERTICAL_AXIS_BOUND_MULIPLIER;
            if (!this.fixMinY && curMinY < minYFromDiagrams && Float.isFinite(curMinY)) minYFromDiagrams = curMinY;
            if (!this.fixMaxY && curMaxY > maxYFromDiagrams && Float.isFinite(curMaxY)) maxYFromDiagrams = curMaxY;
        }
        if (!Float.isInfinite(minYFromDiagrams) && !Float.isInfinite(maxYFromDiagrams)) {
            if (this.targetMinY != minYFromDiagrams) {
                this.targetMinY = minYFromDiagrams;
                this.updateUnitYNextFrame = true;
            }
            if (this.targetMaxY != maxYFromDiagrams) {
                this.targetMaxY = maxYFromDiagrams;
                this.updateUnitYNextFrame = true;
            }
        }
    }

    private static void drawThinLine(GuiGraphics guiGraphics, float x1, float y1, float x2, float y2, float r, float g, float b, float a) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();

        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.lineWidth((float) Minecraft.getInstance().getWindow().getGuiScale() / 4.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        builder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        builder.vertex(pose.last().pose(), x1, y1, 0).color(r, g, b, a).normal(x2-x1, y2-y1, 0).endVertex();
        builder.vertex(pose.last().pose(), x2, y2, 0).color(r, g, b, a).normal(x2-x1, y2-y1, 0).endVertex();

        tesselator.end();
        RenderSystem.disableBlend();
        pose.popPose();
    }

    private float unitXForReferenceLine = Float.NaN;
    private float unitYForReferenceLine = Float.NaN;
    private boolean updateUnitYNextFrame = false;
    private long lastInterpolationTime = 0;
    private void drawReferenceLine(GuiGraphics guiGraphics, float horizontalAxisNameWidth, float verticalAxisNameHeight) {
        PoseStack pose = guiGraphics.pose();

        int color = 0xBF000000 | this.parent.getColor();
        float r = (float) FastColor.ARGB32.red(color) / 255.0f;
        float g = (float) FastColor.ARGB32.green(color) / 255.0f;
        float b = (float) FastColor.ARGB32.blue(color) / 255.0f;
        float a = (float) FastColor.ARGB32.alpha(color) / 255.0f;

        if (Float.isNaN(unitXForReferenceLine)) {
            this.unitXForReferenceLine = calcTick(this.minX, this.maxX, this.maxHorizontalTick);
        }

        float minYToDraw = this.minY;
        float maxYToDraw = this.maxY;
        float tempTargetMinY = this.targetMinY;
        float tempTargetMaxY = this.targetMaxY;
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            minYToDraw = this.minY * (1 - SMOOTH_FACTOR) + tempTargetMinY * SMOOTH_FACTOR;
            maxYToDraw = this.maxY * (1 - SMOOTH_FACTOR) + tempTargetMaxY * SMOOTH_FACTOR;
            if (level.getGameTime() != this.lastInterpolationTime) {
                float diffMin = Math.abs(tempTargetMinY - this.minY);
                float diffMax = Math.abs(tempTargetMaxY - this.maxY);
                if (diffMin == 0 && diffMax == 0) {
                    // Take no action
                } else if (diffMin < 1e-5f && diffMax < 1e-5f) {
                    this.minY = tempTargetMinY;
                    this.maxY = tempTargetMaxY;
                    this.updateUnitYNextFrame = true;
                } else {
                    this.minY = minYToDraw;
                    this.maxY = maxYToDraw;
                    this.updateUnitYNextFrame = true;
                }
                this.lastInterpolationTime = level.getGameTime();
            } else {
                float partialTick = Minecraft.getInstance().getPartialTick();
                minYToDraw = this.minY * (1 - partialTick) + minYToDraw * (partialTick);
                maxYToDraw = this.maxY * (1 - partialTick) + maxYToDraw * (partialTick);
            }
        }

        if (this.updateUnitYNextFrame || Float.isNaN(this.unitYForReferenceLine)) {
            this.unitYForReferenceLine = calcTick(minYToDraw, maxYToDraw, this.maxVerticalTick);
            this.updateUnitYNextFrame = false;
        }

        Font font = Minecraft.getInstance().font;
        int lineHeight = font.lineHeight;
        float quarterLineHeight = 0.25f * lineHeight;
        float vAxisTagLimit = quarterLineHeight + verticalAxisNameHeight + 0.25f;
        for (float i = this.minX; i <= this.maxX; i += this.unitXForReferenceLine) {
            float curX = (i - this.minX) / (this.maxX - this.minX) * this.width;
            drawThinLine(guiGraphics, curX, 0, curX, this.height, r, g, b, a);

            Component toDraw = Component.literal(this.horizontalTickLabel.getTickLabel(i));
            if (curX + 0.25f * font.width(toDraw) > horizontalAxisNameWidth) continue;
            pose.pushPose();
            pose.translate(curX + 1, this.height - 1, 0);
            pose.scale(0.25f, 0.25f, 1.0f);
            guiGraphics.drawString(font, toDraw, 0, -lineHeight - 1, this.parent.getColor(), false);
            pose.popPose();
        }
        for (float j = minYToDraw + this.unitYForReferenceLine; j <= maxYToDraw; j += this.unitYForReferenceLine) {
            float curY = (maxYToDraw - j) / (maxYToDraw - minYToDraw) * this.height;
            drawThinLine(guiGraphics, 0, curY, this.width, curY, r, g, b, a);

            if (curY < vAxisTagLimit) continue;
            pose.pushPose();
            pose.translate(1, curY, 0);
            pose.scale(0.25f, 0.25f, 1f);
            guiGraphics.drawString(font, Component.literal(this.verticalTickLabel.getTickLabel(j)), 1, -lineHeight - 1, this.parent.getColor(), false);
            pose.popPose();
        }
    }

    public DiagramGraphElement horizontalAxisName(Component name) {
        this.horizontalAxisName = name;
        return this;
    }

    public DiagramGraphElement verticalAxisName(Component name) {
        this.verticalAxisName = name;
        return this;
    }

    public DiagramGraphElement domain(float minX, float maxX) {
        this.minX = minX;
        this.maxX = maxX;
        return this;
    }

    public DiagramGraphElement range(float minY, float maxY) {
        this.minY = minY;
        this.maxY = maxY;
        this.minMaxY = maxY;
        this.maxMinY = minY;
        this.fixMaxY = true;
        this.fixMinY = true;
        return this;
    }

    public DiagramGraphElement timeAsHorizontalAxis() {
        this.horizontalAxisIsTime = true;
        return this;
    }

    public DiagramGraphElement timeOrigin(LongSupplier supplier) {
        this.timeOrigin = supplier;
        return this;
    }

    public DiagramGraphElement scatterDiagram(Deque<Vec2> location) {
        this.diagrams.addLast(new ScatterDiagram(location));
        return this;
    }

    /**
     * Automatically set the diagram's vertical axis' range by last diagram added.
     * @param maxLowerBound The lower bound of the vertical axis cannot go greater than this value.
     * @param minUpperBound The upper bound of the vertical axis cannot go less than this value.
     * @param fixLowerBound Fix the vertical axis' lower bound to the value of {@link maxLowerBound}
     *                      (For example, when displaying something that is always positive, you can fix the lower bound to 0)
     * @param fixUpperBound Fix the vertical axis' upper bound to the value of {@link minUpperBound}
     * @return
     */
    public DiagramGraphElement autoYRange(float maxLowerBound, float minUpperBound, boolean fixLowerBound, boolean fixUpperBound) {
        this.maxMinY = maxLowerBound;
        this.minMaxY = minUpperBound;
        this.fixMaxY = fixUpperBound;
        this.fixMinY = fixLowerBound;
        return this;
    }

    @FunctionalInterface
    public static interface AxisTickLabel {
        String getTickLabel(float x);
    }

    public DiagramGraphElement labelHorizontalTick(AxisTickLabel function) {
        this.horizontalTickLabel = function;
        return this;
    }

    public DiagramGraphElement labelVerticalTick(AxisTickLabel function) {
        this.verticalTickLabel = function;
        return this;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    private static float calcTick(float minData, float maxData, int maxTickOnScreen) {
        float diff = maxData - minData;
        float minUnit = diff / maxTickOnScreen;
        float logged = (float) Math.log10(minUnit);
        int exponential = Mth.floor(logged);
        float remainder = logged - exponential;

        float result;
        if (remainder < 0.30102999566398119521373889472449f) {
            result = (float) (2.0f * Math.pow(10, exponential));
        } else if (remainder < 0.69897000433601880478626110527551f) {
            result = (float) (5.0f * Math.pow(10, exponential));
        } else {
            result = (float) Math.pow(10, 1 + exponential);
        }

        if (result <= 0) {
            LOGGER.error("Calculated a wrong tick: {}", result);
            return 1.0f;
        }
        return result;
    }

    private static abstract class Diagram {
        Deque<Vec2> data;
        float minY;
        float maxY;
        public abstract void draw(GuiGraphics guiGraphics, DiagramGraphElement parent, float minX, float maxX);
        public Vec2 processPoint(DiagramGraphElement parent, float minX, float maxX, Vec2 point) {
            float x = point.x;
            float y = point.y;

            float transformedX = ((x - minX) / (maxX - minX) * (parent.width - 1));
            float transformedY = ((1 - (y - parent.minY) / (parent.maxY - parent.minY)) * (parent.height - 1));

            return new Vec2(transformedX, transformedY);
        }
        protected void cleanOldData(float minX) {
            while (!this.data.isEmpty() && this.data.peekFirst().x < minX) {
                this.data.pollFirst();
            }
        }
    }

    private static class ScatterDiagram extends Diagram {
        public ScatterDiagram(Deque<Vec2> data) {
            this.data = data;
        }
        @Override
        public void draw(GuiGraphics guiGraphics, DiagramGraphElement parent, float minX, float maxX) {
            int color = 0xFF000000 | parent.parent.getColor();
            float minY = Float.POSITIVE_INFINITY;
            float maxY = Float.NEGATIVE_INFINITY;
            this.cleanOldData(minX);

            PoseStack pose = guiGraphics.pose();
            pose.pushPose();

            RenderSystem.setShader(GameRenderer::getRendertypeGuiShader);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder builder = tesselator.getBuilder();
            float half = 0.5f;
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            for (Vec2 point : this.data) {
                if (point.y < minY) minY = point.y;
                if (point.y > maxY) maxY = point.y;
                Vec2 transformed = this.processPoint(parent, minX, maxX, point);

                float x = transformed.x;
                float y = transformed.y + half;

                if (x < 0f) break;
                if (x > (float) parent.width) continue;
                if (y < 0f || y > (float) parent.height - 1) continue;

                builder.vertex(pose.last().pose(), x - half, y + half, 0).color(color).endVertex();
                builder.vertex(pose.last().pose(), x + half, y + half, 0).color(color).endVertex();
                builder.vertex(pose.last().pose(), x + half, y - half, 0).color(color).endVertex();
                builder.vertex(pose.last().pose(), x - half, y - half, 0).color(color).endVertex();
            }

            tesselator.end();
            pose.popPose();

            this.minY = minY;
            this.maxY = maxY;
        }
    }

}
