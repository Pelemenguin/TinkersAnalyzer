package pelemenguin.tinkersanalyzer.client.util.render;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.GameRenderer;

/**
 * A helper class used for drawing quads.
 * When using this class, first call {@link #prepareDrawQuads()} to initialize,
 * then use the other methods for drawing,
 * and finally call {@link #finishDrawQuads()} to complete drawing.
 */
public final class QuadHelper {

    private static Tesselator tesselator;
    private static BufferBuilder builder;
    private static boolean isStarted;

    /**
     * Initializes {@link QuadHelper}.
     */
    public static void prepareDrawQuads() {
        assertStatus(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        tesselator = Tesselator.getInstance();
        builder = tesselator.getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        isStarted = true;
    }

    /**
     * Draw a quad according to four given vertex coordinates.
     * 
     * @param x1     X-coordinate of vertex 1
     * @param y1     Y-coordinate of vertex 1
     * @param x2     X-coordinate of vertex 2
     * @param y2     Y-coordinate of vertex 1
     * @param x3     X-coordinate of vertex 3
     * @param y3     Y-coordinate of vertex 1
     * @param x4     X-coordinate of vertex 4
     * @param y4     Y-coordinate of vertex 1
     * @param matrix The current transformation matrix. Usually obtained via {@code poseStack.last().pose()}
     * @param color  The color of the quad in {@code 0xAARRGGBB} format
     */
    public static void drawQuad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, Matrix4f matrix, int color) {
        assertStatus(true);
        builder.vertex(matrix, x1, y1, 0).color(color).endVertex();
        builder.vertex(matrix, x2, y2, 0).color(color).endVertex();
        builder.vertex(matrix, x3, y3, 0).color(color).endVertex();
        builder.vertex(matrix, x4, y4, 0).color(color).endVertex();
    }

    /**
     * Draw an axis-aligned quadrilateral based on the two given vertex coordinates.
     * 
     * @param x1     X-coordinate of the top-left vertex
     * @param y1     Y-coordinate of the top-left vertex
     * @param x2     X-coordinate of the bottom-right vertex
     * @param y2     Y-coordinate of the bottom-right vertex
     * @param matrix The current transformation matrix. Usually obtained via {@code poseStack.last().pose()}
     * @param color  The color of the quad in {@code 0xAARRGGBB} format
     */
    public static void drawAxisAlignedQuad(float x1, float y1, float x2, float y2, Matrix4f matrix, int color) {
        drawQuad(x1, y1, x1, y2, x2, y2, x2, y1, matrix, color);
    }

    /**
     * Draw an axis-aligned quadrilateral based on the two given vertex coordinates.
     * The quad is constrained within a rectangular area.
     * 
     * @param x1     X-coordinate of the top-left vertex
     * @param y1     Y-coordinate of the top-left vertex
     * @param x2     X-coordinate of the bottom-right vertex
     * @param y2     Y-coordinate of the bottom-right vertex
     * @param minX   The x-coordinate of the left boundary of the rectangular area
     * @param minY   The y-coordinate of the top boundary of the rectangular area
     * @param maxX   The x-coordinate of the right boundary of the rectangular area
     * @param maxX   The y-coordinate of the bottom boundary of the rectangular area
     * @param matrix The current transformation matrix. Usually obtained via {@code poseStack.last().pose()}
     * @param color  The color of the quad in {@code 0xAARRGGBB} format
     * @return      {@code true} if any contents are drawn. {@code false} if no content is drawn, which means the whole quad is outside the rectangular area.
     */
    public static boolean drawAxisAlignedQuadWithin(float x1, float y1, float x2, float y2, float minX, float minY, float maxX, float maxY, Matrix4f matrix, int color) {
        assertStatus(true);
        if (x1 < minX && x2 < minX) return false;
        if (x1 > maxX && x2 > maxX) return false;
        if (y1 < minY && y2 < minY) return false;
        if (y1 > maxY && y2 > maxY) return false;

        float x1a;
        float x2a;
        if (x1 <= x2) {
            x1a = minX > x1 ? minX : x1;
            x2a = maxX < x2 ? maxX : x2;
        } else {
            x1a = maxX < x1 ? maxX : x1;
            x2a = minX > x2 ? minX : x2;
        }

        float y1a;
        float y2a;
        if (y1 <= y2) {
            y1a = minY > y1 ? minY : y1;
            y2a = maxY < y2 ? maxY : y2;
        } else {
            y1a = maxY < y1 ? maxY : y1;
            y2a = minY > y2 ? minY : y2;
        }

        drawAxisAlignedQuad(x1a, y1a, x2a, y2a, matrix, color);
        return true;
    }

    /**
     * Draw a bordered, axis-aligned quadrilateral based on the two given vertex coordinates.
     * 
     * @param x1          X-coordinate of the top-left vertex
     * @param y1          Y-coordinate of the top-left vertex
     * @param x2          X-coordinate of the bottom-right vertex
     * @param y2          Y-coordinate of the bottom-right vertex
     * @param borderWidth The width of the border
     * @param matrix      The current transformation matrix. Usually obtained via {@code poseStack.last().pose()}
     * @param color       The color inside the quad in {@code 0xAARRGGBB} format
     * @param colorBorder The color of the border in {@code 0xAARRGGBB} format
     */
    public static void drawAxisAlignedBorderedQuad(float x1, float y1, float x2, float y2, float borderWidth, Matrix4f matrix, int colorInner, int colorBorder) {
        assertStatus(true);
        drawAxisAlignedQuad(x1, y1, x2, y2, matrix, colorInner);

        drawAxisAlignedQuad(x1, y1, x2, y1 + borderWidth, matrix, colorBorder); // Top
        drawAxisAlignedQuad(x1, y2 - borderWidth, x2, y2, matrix, colorBorder); // Bottom
        drawAxisAlignedQuad(x1, y1, x1 + borderWidth, y2, matrix, colorBorder); // Left
        drawAxisAlignedQuad(x2 - borderWidth, y1, x2, y2, matrix, colorBorder); // Right
    }

    /**
     * Draw a bordered, axis-aligned quadrilateral based on the two given vertex coordinates.
     * The quad is constrained within a rectangular area.
     * 
     * @param x1          X-coordinate of the top-left vertex
     * @param y1          Y-coordinate of the top-left vertex
     * @param x2          X-coordinate of the bottom-right vertex
     * @param y2          Y-coordinate of the bottom-right vertex
     * @param minX        The x-coordinate of the left boundary of the rectangular area
     * @param minY        The y-coordinate of the top boundary of the rectangular area
     * @param maxX        The x-coordinate of the right boundary of the rectangular area
     * @param maxX        The y-coordinate of the bottom boundary of the rectangular area
     * @param borderWidth The width of the border
     * @param matrix      The current transformation matrix. Usually obtained via {@code poseStack.last().pose()}
     * @param color       The color inside the quad in {@code 0xAARRGGBB} format
     * @param colorBorder The color of the border in {@code 0xAARRGGBB} format
     */
    public static void drawAxisAlignedBorderedQuadWithin(float x1, float y1, float x2, float y2, float minX, float minY, float maxX, float maxY, float edgeWidth, Matrix4f matrix, int colorInner, int colorEdge) {
        assertStatus(true);
        drawAxisAlignedQuadWithin(x1, y1, x2, y2, minX, minY, maxX, maxY, matrix, colorInner);

        drawAxisAlignedQuadWithin(x1, y1, x2, y1 + edgeWidth, minX, minY, maxX, maxY, matrix, colorEdge); // Top
        drawAxisAlignedQuadWithin(x1, y2 - edgeWidth, x2, y2, minX, minY, maxX, maxY, matrix, colorEdge); // Bottom
        drawAxisAlignedQuadWithin(x1, y1, x1 + edgeWidth, y2, minX, minY, maxX, maxY, matrix, colorEdge); // Left
        drawAxisAlignedQuadWithin(x2 - edgeWidth, y1, x2, y2, minX, minY, maxX, maxY, matrix, colorEdge); // Right
    }

    /**
     * Finishes quad drawing.
     */
    public static void finishDrawQuads() {
        assertStatus(true);
        tesselator.end();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        tesselator = null;
        builder = null;
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

    private QuadHelper() {}

}
