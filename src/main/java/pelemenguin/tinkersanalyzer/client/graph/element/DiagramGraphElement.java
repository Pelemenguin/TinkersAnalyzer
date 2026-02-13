package pelemenguin.tinkersanalyzer.client.graph.element;

import java.util.ArrayList;
import java.util.Deque;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec2;
import pelemenguin.tinkersanalyzer.client.graph.AnalyzerGraph;

public class DiagramGraphElement extends AnalyzerGraphElement {

    AnalyzerGraph parent;
    int width;
    int height;

    Component horizontalAxisName;
    Component verticalAxisName;
    float minX;
    float maxX;
    boolean horizontalAxisIsTime;

    public DiagramGraphElement(AnalyzerGraph parent, int width, int height) {
        this.width = width;
        this.height = height;
        this.parent = parent;
    }

    ArrayList<Diagram> diagrams = new ArrayList<>();

    @Override
    public void draw(GuiGraphics guiGraphics) {
        guiGraphics.fill(0, 0, 1, this.height, 0xFF000000 | this.parent.getColor());
        guiGraphics.fill(0, this.height - 1, this.width, this.height, 0xFF000000 | this.parent.getColor());

        PoseStack pose = guiGraphics.pose();
        Font font = Minecraft.getInstance().font;
        int lineHeight = font.lineHeight;
        if (this.horizontalAxisName != null) {
            int width = font.width(this.horizontalAxisName);
            pose.pushPose();
            pose.translate(this.width - 1, this.getHeight() - 2, 0);
            pose.scale(0.5f, 0.5f, 1.0f);
            guiGraphics.drawString(font, this.horizontalAxisName, -width, -lineHeight, this.parent.getColor(), false);
            pose.popPose();
        }
        if (this.verticalAxisName != null) {
            pose.pushPose();
            pose.translate(2, 1, 0);
            pose.scale(0.5f, 0.5f, 1.0f);
            guiGraphics.drawString(font, this.verticalAxisName, 0, 0, this.parent.getColor(), false);
            pose.popPose();
        }

        drawReferenceLine(guiGraphics, 5, 3);

        float minX = this.minX;
        float maxX = this.maxX;
        if (this.horizontalAxisIsTime) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                float curTime = level.getGameTime() + Minecraft.getInstance().getPartialTick();
                minX += curTime;
                maxX += curTime;
            }
        }
        for (Diagram diagram : this.diagrams) {
            diagram.draw(guiGraphics, this, minX, maxX);
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

    private void drawReferenceLine(GuiGraphics guiGraphics, int vLineCount, int hLineCount) {
        float unitX = (float) this.width / vLineCount;
        float unitY = (float) this.height / hLineCount;

        int color = 0xBF000000 | this.parent.getColor();
        float r = (float) FastColor.ARGB32.red(color) / 255.0f;
        float g = (float) FastColor.ARGB32.green(color) / 255.0f;
        float b = (float) FastColor.ARGB32.blue(color) / 255.0f;
        float a = (float) FastColor.ARGB32.alpha(color) / 255.0f;

        for (int i = 1; i <= vLineCount; i++) {
            float curX = (unitX * i);
            drawThinLine(guiGraphics, curX, 0, curX, this.height, r, g, b, a);
        }
        for (int j = 0; j < hLineCount; j++) {
            float curY = (unitY * j);
            drawThinLine(guiGraphics, 0, curY, this.width, curY, r, g, b, a);
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

    public DiagramGraphElement timeAsHorizontalAxis() {
        this.horizontalAxisIsTime = true;
        return this;
    }

    public DiagramGraphElement scatterDiagram(Deque<Vec2> location) {
        this.diagrams.add(new ScatterDiagram(location));
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

    private static abstract class Diagram {
        float minY;
        float maxY;
        public abstract void draw(GuiGraphics guiGraphics, DiagramGraphElement parent, float minX, float maxX);
        public Vec2 processPoint(DiagramGraphElement parent, float minX, float maxX, Vec2 point) {
            float x = point.x;
            float y = point.y;


            int transformedX = (int) ((x - minX) / (maxX - minX) * parent.width);
            int transformedY = (int) ((1 - (y - this.minY) / (this.maxY - this.minY)) * parent.height);

            return new Vec2(transformedX, transformedY);
        }
    }

    private static class ScatterDiagram extends Diagram {
        Deque<Vec2> data;
        public ScatterDiagram(Deque<Vec2> data) {
            this.data = data;
            // TODO: Remove this after test
            this.minY = 0;
            this.maxY = 10;
        }
        @Override
        public void draw(GuiGraphics guiGraphics, DiagramGraphElement parent, float minX, float maxX) {
            int color = 0xFF000000 | parent.parent.getColor();
            for (Vec2 point : this.data) {
                Vec2 transformed = this.processPoint(parent, minX, maxX, point);

                int x = (int) transformed.x;
                int y = (int) transformed.y;

                if (x < 0) break;
                if (x > parent.width) continue;

                guiGraphics.fill(x, y, x+1, y+1, color);
            }
            while (!this.data.isEmpty() && this.data.peekFirst().x < minX) {
                this.data.pollFirst();
            }
        }
    }

}
