package pelemenguin.tinkersanalyzer.client.util;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;

/**
 * A helper class used for drawing lines.
 * When using this class, first call {@link #prepareDrawLine()} to initialize,
 * then use the other methods for drawing,
 * and finally call {@link #finishDrawLine()} to complete drawing.
 */
public final class LineHelper {

    private static Tesselator tesselator;
    private static BufferBuilder builder;
    private static boolean isStarted = false;

    /**
     * Initialize the {@link LineHelper}.
     */
    public static void prepareDrawLine() {
        assertStatus(false);
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        GL20.glEnable(GL20.GL_LINE_SMOOTH);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.0f);
        tesselator = Tesselator.getInstance();
        builder = tesselator.getBuilder();
        builder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        isStarted = true;
    }

    /**
     * Draw a line segment between two points.
     * 
     * @param x1     The x-coordinate of the first point
     * @param y1     The y-coordinate of the first point
     * @param x2     The x-coordinate of the second point
     * @param y2     The y-coordinate of the second point
     * @param matrix The current transformation matrix. Usually obtained via {@code poseStack.last().pose()}
     * @param color  The color in {@code 0xAARRGGBB} format
     */
    public static void connect(float x1, float y1, float x2, float y2, Matrix4f matrix, int color) {
        assertStatus(true);

        float resultDx = x2 - x1;
        float resultDy = y2 - y1;

        builder.vertex(matrix, x1, y1, 0).color(color).normal(resultDx, resultDy, 0).endVertex();
        builder.vertex(matrix, x2, y2, 0).color(color).normal(resultDx, resultDy, 0).endVertex();
    }

    /**
     * Draw a line segment between two points.
     * The line segment is constrained within a rectangular area.
     * 
     * @param x1     The x-coordinate of the first point
     * @param y1     The y-coordinate of the first point
     * @param x2     The x-coordinate of the second point
     * @param y2     The y-coordinate of the second point
     * @param minX   The minimum x-coordinate that the line segment can reach
     * @param minY   The minimum y-coordinate that the line segment can reach
     * @param maxX   The maximum x-coordinate that the line segment can reach
     * @param maxX   The maximum y-coordinate that the line segment can reach
     * @param matrix The current transformation matrix. Usually obtained via {@code poseStack.last().pose()}
     * @param color  The color in {@code 0xAARRGGBB} format
     * @return       {@code true} if any contents are drawn. {@code false} if no content is drawn, which means the whole segment is outside the rectangular area.
     */
    public static boolean connectWithin(float x1, float y1, float x2, float y2, float minX, float minY, float maxX, float maxY, Matrix4f matrix, int color) {
        assertStatus(true);
        float startX;
        float startY;
        float endX;
        float endY;

        if (Float.isInfinite(x1)) {
            if (x2 == x1) {
                return false;
            } else if (x2 == -x1) {
                startX = minX;
                endX = maxX;
                startY = endY = (y1 + y2) * 0.5f;
            } else {
                if (y2 > maxY || y2 < minY) {
                    return false;
                }
                startX = x1 > 0 ? maxX : minX;
                endX = Mth.clamp(x2, minX, maxX);
                startY = endY = y2;
            }
        } else if (Float.isInfinite(x2)) {
            if (y1 > maxY || y1 < minY) {
                return false;
            }
            startX = Mth.clamp(x1, minX, maxX);
            endX = x2 > 0 ? maxX : minX;
            startY = endY = y1;
        } else {
            float dx = x2 - x1;
            float dy = y2 - y1;

            // Consider line l: p1 + t * (p2 - p1)
            float minT = 0.0f;
            float maxT = 1.0f;

            // Left border: x1 + t * dx >= minX
            // Right border: x1 + t * dx <= maxX
            if (dx > 0) {
                minT = Math.max(minT, (minX - x1) / dx);
                maxT = Math.min(maxT, (maxX - x1) / dx);
            } else if (dx < 0) {
                maxT = Math.min(maxT, (minX - x1) / dx);
                minT = Math.max(minT, (maxX - x1) / dx);
            } else {
                if (x1 < minX || x1 > maxX) {
                    return false;
                }
            }

            // Top border: y1 + t * dy >= minY
            // Bottom border: y1 + t * dy <= maxY
            if (dy > 0) {
                minT = Math.max(minT, (minY - y1) / dy);
                maxT = Math.min(maxT, (maxY - y1) / dy);
            } else if (dy < 0) {
                maxT = Math.min(maxT, (minY - y1) / dy);
                minT = Math.max(minT, (maxY - y1) / dy);
            } else {
                if (y1 < minY || y1 > maxY) {
                    return false;
                }
            }

            if (maxT <= minT) return false;

            startX = x1 + minT * dx;
            startY = y1 + minT * dy;
            endX = x1 + maxT * dx;
            endY = y1 + maxT * dy;
        }

        connect(startX, startY, endX, endY, matrix, color);
        return true;
    }

    /**
     * Finishes the {@link LineHelper}.
     */
    public static void finishDrawLine() {
        assertStatus(true);
        tesselator.end();
        GL20.glDisable(GL20.GL_LINE_SMOOTH);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        isStarted = false;
    }

    private static void assertStatus(boolean shouldStarted) {
        if (shouldStarted != isStarted) {
            throw new IllegalStateException("Wrong state! It %s but actually it %s".formatted(
                    shouldStarted ? "should start already" : "shouldn't start yet",
                    isStarted ? "did" : "didn't"
                ));
        }
    }

    private LineHelper() {}

}
