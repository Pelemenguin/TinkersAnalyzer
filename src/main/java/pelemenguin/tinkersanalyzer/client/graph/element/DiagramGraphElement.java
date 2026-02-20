package pelemenguin.tinkersanalyzer.client.graph.element;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.LongSupplier;

import org.joml.Matrix4f;
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
import net.minecraft.util.Mth;
import pelemenguin.tinkersanalyzer.client.graph.AnalyzerGraph;
import pelemenguin.tinkersanalyzer.client.util.render.LineHelper;
import pelemenguin.tinkersanalyzer.client.util.render.QuadHelper;

public class DiagramGraphElement extends AnalyzerGraphElement {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final AxisTickLabel DEFAULT_LABEL_FORMAT = (x) -> {
        float abs = Mth.abs(x);
        if (abs >= 1e7f) {
            return "%.1E".formatted(x);
        } else if (abs >= 1e3f) {
            return "%d".formatted((int) x);
        } else if (abs >= 1e2f) {
            return "%.1f".formatted(x);
        } else if (abs >= 1) {
            return "%.2f".formatted(x);
        } else if (abs >= 0.01f) {
            return "%.3f".formatted(x);
        } else if (abs < 5e-5f) {
            return "0";
        } else {
            return "%.4f".formatted(x);
        }
    };

    private static final AxisTickLabel DEFAULT_TICK_LABEL = DEFAULT_LABEL_FORMAT;
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
        Matrix4f matrix = guiGraphics.pose().last().pose();

        QuadHelper.prepareDrawQuads();
        QuadHelper.drawAxisAlignedQuad(0, 0, 1, this.height, matrix, 0xFF000000 | this.parent.getColor());
        QuadHelper.drawAxisAlignedQuad(0, this.height - 1, this.width, this.height, matrix, 0xFF000000 | this.parent.getColor());
        QuadHelper.finishDrawQuads();

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
            float curMinY = diagram.minY * VERTICAL_AXIS_BOUND_MULIPLIER * diagram.scale;
            float curMaxY = diagram.maxY * VERTICAL_AXIS_BOUND_MULIPLIER * diagram.scale;
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

        // Throw away cache every frame
        this.cachedMinYForThisFrame = Float.NaN;
        this.cachedMaxYForThisFrame = Float.NaN;
    }

    private float cachedMinYForThisFrame = Float.NaN;
    private float cachedMaxYForThisFrame = Float.NaN;
    private void calcYRangeForThisFrame() {
        if (!Float.isNaN(this.cachedMinYForThisFrame) && !Float.isNaN(this.cachedMaxYForThisFrame)) return;

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

        this.cachedMinYForThisFrame = minYToDraw;
        this.cachedMaxYForThisFrame = maxYToDraw;
    }

    private float unitXForReferenceLine = Float.NaN;
    private float unitYForReferenceLine = Float.NaN;
    private boolean updateUnitYNextFrame = false;
    private long lastInterpolationTime = 0;
    private void drawReferenceLine(GuiGraphics guiGraphics, float horizontalAxisNameWidth, float verticalAxisNameHeight) {
        PoseStack pose = guiGraphics.pose();
        Matrix4f matrix = pose.last().pose();

        int color = 0x7F000000 | this.parent.getColor();

        if (Float.isNaN(unitXForReferenceLine)) {
            this.unitXForReferenceLine = calcTick(this.minX, this.maxX, this.maxHorizontalTick);
        }

        calcYRangeForThisFrame();
        float minYToDraw = this.cachedMinYForThisFrame;
        float maxYToDraw = this.cachedMaxYForThisFrame;

        Font font = Minecraft.getInstance().font;
        int lineHeight = font.lineHeight;
        float quarterLineHeight = 0.25f * lineHeight;
        float vAxisTagLimit = quarterLineHeight + verticalAxisNameHeight + 0.25f;
        float width = (float) Minecraft.getInstance().getWindow().getGuiScale() * 0.0625f;

        for (float i = chooseStartTick(this.minX, this.unitXForReferenceLine); i < this.maxX; i += this.unitXForReferenceLine) {
            float curX = (i - this.minX) / (this.maxX - this.minX) * this.width;
            if (curX >= this.width - 1.25f) continue;
            QuadHelper.prepareDrawQuads();
            QuadHelper.drawAxisAlignedQuad(curX, 0, curX + width, this.height, matrix, color);
            QuadHelper.finishDrawQuads();

            Component toDraw = Component.literal(this.horizontalTickLabel.getTickLabel(i));
            if (curX + 0.25f * font.width(toDraw) > horizontalAxisNameWidth) continue;
            pose.pushPose();
            pose.translate(curX + 1, this.height - 1, 0);
            pose.scale(0.25f, 0.25f, 1.0f);
            guiGraphics.drawString(font, toDraw, 0, -lineHeight - 1, this.parent.getColor(), false);
            pose.popPose();
        }
        final float mysteriousConstant = this.height - 1.25f - font.lineHeight * 0.25f;
        for (float j = chooseStartTick(minYToDraw, this.unitYForReferenceLine); j < maxYToDraw; j += this.unitYForReferenceLine) {
            float curY = (maxYToDraw - j) / (maxYToDraw - minYToDraw) * this.height;
            if (curY <= 0.25f) continue;
            if (curY > this.height - 1.25f) continue;
            QuadHelper.prepareDrawQuads();
            QuadHelper.drawAxisAlignedQuad(0, curY, this.width, curY + width, matrix, color);
            QuadHelper.finishDrawQuads();

            if (curY > mysteriousConstant) continue;
            if (curY < vAxisTagLimit) continue;
            pose.pushPose();
            pose.translate(1, curY, 0);
            pose.scale(0.25f, 0.25f, 1f);
            guiGraphics.drawString(font, Component.literal(this.verticalTickLabel.getTickLabel(j)), 1, -lineHeight - 1, this.parent.getColor(), false);
            pose.popPose();
        }

        QuadHelper.prepareDrawQuads();
        QuadHelper.drawAxisAlignedQuad(this.width - width, 0, this.width, this.height, matrix, color);
        QuadHelper.drawAxisAlignedQuad(0, 0, this.width, width, matrix, color);
        QuadHelper.finishDrawQuads();
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

    /**
     * Add a scatter diagram to the {@link DiagramGraphElement}.
     * 
     * @param location A {@link Deque}&lt;{@link DataPoint}&gt; to storage the points to draw.
     *                 The field {@link DataPoint#x} is for the x-coordinate of the point,
     *                 while the field {@link DataPoint#y} is for the y-coordinate of the point.
     * 
     *                 <p>
     *                 Remember to <b>keep the reference</b> of this Deque.
     *                 You can directly add data to this Deque,
     *                 but make sure the x of the DataaPoints are sorted in acsending order.
     *                 Points whose x is to small to be drawn on the screen will be removed by {@link Deque#pollFirst}.
     * 
     *                 <p>
     *                 Usually, inserting data and rendering data run on the same thread (Render Thread).
     *                 If you wish to insert data on a different thread, pass in a concurrent Deque.
     * @return The DiagramGraphElement itself
     */
    public DiagramGraphElement scatterDiagram(Deque<DataPoint> location) {
        this.diagrams.addLast(new ScatterDiagram(location));
        return this;
    }

    /**
     * Add a histogram to the {@link DiagramGraphElement}.
     * 
     * @param location A {@link Deque}&lt;{@link HistogramBar}&gt; to storage the bars to draw.
     * @return
     */
    public DiagramGraphElement histogram(Deque<HistogramBar> location) {
        this.diagrams.addLast(new Histogram(location));
        return this;
    }

    public DiagramGraphElement lineGraph(Deque<DataPoint> location) {
        this.diagrams.addLast(new LineGraph(location));
        return this;
    }

    public DiagramGraphElement scaleLastDiagram(float scale) {
        this.diagrams.getLast().scale = scale;
        return this;
    }

    public DiagramGraphElement colorLastDiagram(int color) {
        this.diagrams.getLast().color = color;
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

    private static float chooseStartTick(float min, float tick) {
        return (float) Math.ceil(min / tick) * tick;
    }

    private static abstract class Diagram {
        float minY;
        float maxY;
        float scale = 1.0f;
        int color = -1;
        public abstract void draw(GuiGraphics guiGraphics, DiagramGraphElement parent, float minX, float maxX);
        public float transformX(DiagramGraphElement parent, float minX, float maxX, float x) {
            return ((x - minX) / (maxX - minX) * (parent.width));
        }
        public float transformY(DiagramGraphElement parent, float y) {
            return ((1 - (y - parent.cachedMinYForThisFrame) / (parent.cachedMaxYForThisFrame - parent.cachedMinYForThisFrame) * this.scale) * (parent.height));
        }
    }

    public static record DataPoint(float x, float y) {}
    private static class ScatterDiagram extends Diagram {
        Deque<DataPoint> data;
        public ScatterDiagram(Deque<DataPoint> data) {
            this.data = data;
        }
        void cleanOldData(float minX) {
            while (!this.data.isEmpty() && this.data.peekFirst().x < minX) {
                this.data.pollFirst();
            }
        }
        @Override
        public void draw(GuiGraphics guiGraphics, DiagramGraphElement parent, float minX, float maxX) {
            int color = 0xFF000000 | (this.color < 0 ? parent.parent.getColor() : this.color);
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

            for (DataPoint point : this.data) {
                if (point.y < minY) minY = point.y;
                if (point.y > maxY) maxY = point.y;

                float x = this.transformX(parent, minX, maxX, point.x);
                float y = this.transformY(parent, point.y);

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

    public static record HistogramBar(float leftX, float rightX, float y) {
        public float middleX() {
            return (this.leftX + this.rightX) * 0.5f;
        }
    }
    private static class Histogram extends Diagram {
        Deque<HistogramBar> data;
        public Histogram(Deque<HistogramBar> data) {
            this.data = data;
        }
        void cleanOldData(float minX) {
            while (!this.data.isEmpty() && this.data.peekFirst().rightX < minX) {
                this.data.pollFirst();
            }
        }
        @Override
        public void draw(GuiGraphics guiGraphics, DiagramGraphElement parent, float minX, float maxX) {
            int colorInner = 0x7F000000 | (this.color < 0 ? parent.parent.getColor() : this.color);
            int color = 0xFF000000 | colorInner;
            float minY = Float.POSITIVE_INFINITY;
            float maxY = Float.NEGATIVE_INFINITY;
            this.cleanOldData(minX);

            PoseStack pose = guiGraphics.pose();
            Matrix4f matrix = pose.last().pose();
            pose.pushPose();

            QuadHelper.prepareDrawQuads();
            float y0 = this.transformY(parent, 0);

            for (HistogramBar bar : this.data) {
                if (bar.y < minY) minY = bar.y;
                if (bar.y > maxY) maxY = bar.y;

                float leftX = this.transformX(parent, minX, maxX, bar.leftX);
                float rightX = this.transformX(parent, minX, maxX, bar.rightX);
                float y = this.transformY(parent, bar.y);

                if (rightX < 0f) break;
                if (leftX > (float) parent.width) continue;
                QuadHelper.drawAxisAlignedBorderedQuadWithin(leftX, y, rightX, y0, 0, 0, parent.width - 1, parent.height - 1, 0.25f, matrix, colorInner, color);
            }

            QuadHelper.finishDrawQuads();
            pose.popPose();

            this.minY = minY;
            this.maxY = maxY;
        }
    }

    private static class LineGraph extends Diagram {
        private Deque<DataPoint> data;
        private static final DataPoint RIGHT_INFINITY = new DataPoint(Float.POSITIVE_INFINITY, 0);
        public LineGraph(Deque<DataPoint> data) {
            this.data = data;
            this.data.addFirst(new DataPoint(Float.NEGATIVE_INFINITY, 0));
        }
        void cleanOldData(float minX) {
            DataPoint leftmost = null;
            while (!this.data.isEmpty() && this.data.peekFirst().x < minX) {
                leftmost = this.data.pollFirst();
            }
            if (leftmost == null) return;
            this.data.addFirst(leftmost);
        }
        @Override
        public void draw(GuiGraphics guiGraphics, DiagramGraphElement parent, float minX, float maxX) {
            int color = 0xFF000000 | (this.color < 0 ? parent.parent.getColor() : this.color);
            float minY = Float.POSITIVE_INFINITY;
            float maxY = Float.NEGATIVE_INFINITY;
            this.cleanOldData(minX);

            PoseStack pose = guiGraphics.pose();
            Matrix4f matrix = pose.last().pose();
            pose.pushPose();

            LineHelper.prepareDrawLine();

            synchronized (this.data) {
                float lastX = this.transformX(parent, minX, maxX, Float.NEGATIVE_INFINITY);
                float lastY = this.transformY(parent, 0);
                this.data.addLast(RIGHT_INFINITY);
                for (DataPoint point : this.data) {
                    float x = this.transformX(parent, minX, maxX, point.x);
                    float y = this.transformY(parent, point.y);

                    LineHelper.connectWithin(lastX, lastY, x, y, 1, 0, parent.width, parent.height - 1, matrix, color);
                    if (point.y < minY) minY = point.y;
                    if (point.y > maxY) maxY = point.y;

                    lastX = x;
                    lastY = y;
                }
                this.data.removeLast();
            }

            LineHelper.finishDrawLine();
            pose.popPose();

            this.minY = minY;
            this.maxY = maxY;
        }
    }

}
